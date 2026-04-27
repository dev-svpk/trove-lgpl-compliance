package solver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class AbstractionSettings extends JPanel {
   private JPanel flopPanel = new JPanel();
   private JPanel turnPanel = new JPanel();
   private JPanel riverPanel = new JPanel();
   private TitledBorder titledBorder;
   static AbstractionSettings instance = new AbstractionSettings();
   private static volatile long[] memoryEstimates = new long[4];
   private static String memoryLabel;

   public AbstractionSettings() {
      this.setLayout(new BoxLayout(this, 1));
      this.setBackground(ThemeManager.BACKGROUND_DARK);
      this.titledBorder = BorderFactory.createTitledBorder("Abstraction");
      this.titledBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      this.setBorder(this.titledBorder);
   }

   public final void refresh() {
      this.removeAll();
      if (AnalysisPanel.getTreeStage() > 0) {
         this.buildHoldemUI();
      } else {
         this.buildOmahaUI();
      }

      MainTabbedPane.setSettingsEnabled(this.isEnabled());
   }

   public static String formatGB(long bytes) {
      return bytes < 200000L ? "0GB" : FlopNE.formatWithPrecision((double)bytes / 1.073741824E9D, 3) + "GB";
   }

   public static String formatMB(long bytes) {
      return (double)bytes < 1048576.0D ? "0MB" : FlopNE.formatWithPrecision((double)bytes / 1048576.0D, 3) + "MB";
   }

   private static long riverBucketMemory() {
      return memoryEstimates != null ? memoryEstimates[3] : TextureAbstractionLookup.getRiverTextureBucketCount(FlopNE.riverTextureType) * (long)FlopNE.riverBuckets;
   }

   private static long turnBucketMemory() {
      return memoryEstimates != null ? memoryEstimates[2] : TextureAbstractionLookup.getTurnTextureBucketCount(FlopNE.turnTextureType) * (long)FlopNE.turnBuckets << 2;
   }

   private static long flopBucketMemory() {
      if (memoryEstimates != null) {
         return memoryEstimates[1];
      } else if (FlopNE.flopBuckets == 0) {
         return AnalysisPanel.isHoldem() ? 881374L : 79791556L;
      } else {
    	  if (AnalysisPanel.gameType == 0) {
    		  return 1755L * (long)FlopNE.flopBuckets << 2;
    	  } else {
    		  return 573L * (long)FlopNE.flopBuckets << 2;
    	  }
      }
   }

   private static boolean avgStreetReaches(int street) {
      return FlopNE.avgStreets + AnalysisPanel.getTreeStage() > street;
   }

   private static boolean evStreetReaches(int street) {
      return FlopNE.evStreets + AnalysisPanel.getTreeStage() > street;
   }

   public final void recalculate() {
      this.recalculateAsync(false);
   }

   private void recalculateAsync(boolean waitForComplete) {
      Thread worker;
      (worker = new Thread(() -> {
         long estimatedMemory = this.estimateTotalMemory();
         long maxHeapMemory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
         if (estimatedMemory > maxHeapMemory) {
            this.titledBorder.setTitleColor(Color.red);
         } else {
            this.titledBorder.setTitleColor(Color.black);
         }

         memoryLabel = estimatedMemory < 0L ? "Specify board cards." : formatGB(estimatedMemory) + " / " + formatGB(maxHeapMemory);
         EventQueue.invokeLater(() -> {
            this.titledBorder.setTitle("Abstraction (" + memoryLabel + ")");
            this.buildFlopSummary().repaint();
            this.buildTurnSummary().repaint();
            this.buildRiverSummary().repaint();
            this.revalidate();
            this.repaint();
         });
      })).start();
      if (waitForComplete) {
         try {
            worker.join();
            return;
         } catch (InterruptedException ie) {
            ie.printStackTrace();
         }
      }

   }

   private static long estimatePostflopMemory(boolean isOmaha) {
      GameState gameState = AnalysisPanel.getRootGameState();
      byte requiredBoardCards = 3;
      if (gameState.gameStage == 2) {
         requiredBoardCards = 4;
      } else if (gameState.gameStage == 3) {
         requiredBoardCards = 5;
      }

      card[] parsedBoardCards;
      if ((parsedBoardCards = card.parseCards(MainTabbedPane.getBoardCardsString())).length < requiredBoardCards) {
         return -1L;
      } else {
         final card[] boardCards = new card[requiredBoardCards];

         for(int i = 0; i < requiredBoardCards; ++i) {
            boardCards[i] = parsedBoardCards[i];
         }

         final GameState activeGameState = AnalysisPanel.getRootGameState();
         final long[][] perPlayerStreetCounts = new long[activeGameState.nWay][4];
         ArrayList workers = new ArrayList();
         if (memoryEstimates == null) {
            memoryEstimates = new long[4];
         }

         Arrays.fill(memoryEstimates, 0L);

         int playerIdx = 0;
         //for(int var8 = 0; var8 < var19.b; ++var8) {
         while (playerIdx < activeGameState.nWay){
            int capturedIdx = playerIdx++;
            Thread worker;
            worker = new Thread(() -> AbstractionSettings.computeBucketsForPlayerAsync(isOmaha, capturedIdx, activeGameState, boardCards, (long[][])perPlayerStreetCounts));
            worker.start();
            /*(var5 = new Thread(() -> {
               try {
                  int tmp = var8; 
                  int[] var70;
                  if (var0) {
                     var70 = FlopNE.a(GameSettings.d, (OmahaHandRange)AnalysisPanel.getRangeForPlayer(var8), var19.h, var3, GameSettings.c);
                  } else {
                     var70 = FlopNE.a(AnalysisPanel.getRangeForPlayer(var8).d(), var19.h, var3, new collections.LongIntHashMap());
                  }

                  var16[var8][1] = (long)var70[0];
                  var16[var8][2] = (long)var70[1];
                  var16[var8][3] = (long)var70[2];
                  synchronized(f) {
                     long[] var10000 = f;
                     var10000[1] += (long)(var70[0] / var19.b);
                     var10000 = f;
                     var10000[2] += (long)(var70[1] / var19.b);
                     var10000 = f;
                     var10000[3] += (long)(var70[2] / var19.b);
                  }
               } catch (InterruptedException var6) {
               }
            })).start();*/
            workers.add(worker);
         }

         Iterator workerIter = workers.iterator();

         while(workerIter.hasNext()) {
            Thread worker = (Thread)workerIter.next();

            try {
               worker.join();
            } catch (InterruptedException ie) {
               ie.printStackTrace();
            }
         }

         long weightedTotal = 0L;

         for(int playerIndex = 0; playerIndex < activeGameState.nWay; ++playerIndex) {
            for(int street = 1; street < 4; ++street) {
               int evMultiplier = evStreetReaches(street) ? 2 : 0;
               weightedTotal += AnalysisPanel.countNodesAtStreet(street, playerIndex, evMultiplier) * perPlayerStreetCounts[playerIndex][street];
            }
         }

         long memoryBytes = weightedTotal << 3;
         if (isOmaha) {
            memoryBytes = (memoryBytes += 23762752L) + 157286400L * (long)activeGameState.nWay;
         }

         return memoryBytes + 1205862400L * (long)activeGameState.nWay + icmTableMemoryBytes();
      }
   }

   private synchronized long estimateTotalMemory() {
      if (AnalysisPanel.getTreeStage() > 0) {
         return estimatePostflopMemory(!AnalysisPanel.isHoldem());
      } else {
         memoryEstimates = null;
         long flopNodes = AnalysisPanel.countFlopNodes(evStreetReaches(1) ? 2 : 0) + 2L;
         long runningTotal = 0L + flopNodes * flopBucketMemory();
         if (avgStreetReaches(1)) {
            runningTotal += AnalysisPanel.countFlopNodes(0) * flopBucketMemory();
         }

         long turnNodes = AnalysisPanel.countTurnNodes(evStreetReaches(2) ? 2 : 0) + 2L;
         long turnBucketMem = turnBucketMemory();
         runningTotal += turnNodes * turnBucketMem;
         if (avgStreetReaches(2)) {
            runningTotal += AnalysisPanel.countTurnNodes(0) * turnBucketMem;
         }

         long riverNodes = AnalysisPanel.countRiverNodes(evStreetReaches(3) ? 2 : 0) + 2L;
         long riverBucketMem = riverBucketMemory();
         runningTotal += riverNodes * riverBucketMem;
         if (avgStreetReaches(3)) {
            runningTotal += AnalysisPanel.countRiverNodes(0) * riverBucketMem;
         }

         long totalBytes = (totalBytes = runningTotal << 3) + 524288000L;
         if (AnalysisPanel.isHoldem()) {
            totalBytes = (totalBytes += 503316480L) + 262144000L;
         } else {
            totalBytes = (totalBytes += 4215275520L) + 23762752L;
         }

         return (totalBytes += 1048576000L) + icmTableMemoryBytes();
      }
   }

   private static long icmLookupEntryCount() {
      int nWay = AnalysisPanel.getRootGameState().nWay;
      if (!FlopNE.shouldUseChipEV()) {
         int icmTableIndex = FlopNE.icm != null ? 0 : 1;
         return (long) FilterButtonListener.a[icmTableIndex][nWay];
      } else {
         return 0L;
      }
   }

   private static long icmTableMemoryBytes() {
      int nWay = AnalysisPanel.getRootGameState().nWay;
      return !FlopNE.shouldUseChipEV() ? AnalysisPanel.countTerminalNodes() * (long)nWay * icmLookupEntryCount() << 3 : 0L;
   }

   private JPanel buildTurnSummary() {
      this.turnPanel.removeAll();
      long turnNodes = AnalysisPanel.countTurnNodes(0);
      this.turnPanel.add(new JLabel("Turn nodes: " + turnNodes));
      long bucketsPerNode = turnBucketMemory();
      JLabel bucketsLabel = new JLabel("Buckets/node: " + NumberFormat.getNumberInstance(Locale.US).format(bucketsPerNode));
      this.turnPanel.add(bucketsLabel);
      JLabel totalLabel = new JLabel("Total: " + NumberFormat.getNumberInstance(Locale.US).format(turnNodes * bucketsPerNode));
      this.turnPanel.add(totalLabel);
      return this.turnPanel;
   }

   private JPanel buildFlopSummary() {
      this.flopPanel.removeAll();
      long flopNodes = AnalysisPanel.countFlopNodes(0);
      long bucketsPerNode = flopBucketMemory();
      this.flopPanel.add(new JLabel("Flop nodes: " + flopNodes));
      JLabel bucketsLabel = new JLabel("Buckets/node: " + NumberFormat.getNumberInstance(Locale.US).format(bucketsPerNode));
      this.flopPanel.add(bucketsLabel);
      long total = flopNodes * bucketsPerNode;
      JLabel totalLabel = new JLabel("Total: " + NumberFormat.getNumberInstance(Locale.US).format(total));
      this.flopPanel.add(totalLabel);
      return this.flopPanel;
   }

   private JPanel buildRiverSummary() {
      this.riverPanel.removeAll();
      long riverNodes = AnalysisPanel.countRiverNodes(0);
      this.riverPanel.add(new JLabel("River nodes: " + riverNodes));
      long bucketsPerNode = riverBucketMemory();
      JLabel bucketsLabel = new JLabel("Buckets/node: " + NumberFormat.getNumberInstance(Locale.US).format(bucketsPerNode));
      this.riverPanel.add(bucketsLabel);
      JLabel totalLabel = new JLabel("Total: " + NumberFormat.getNumberInstance(Locale.US).format(riverNodes * bucketsPerNode));
      this.riverPanel.add(totalLabel);
      return this.riverPanel;
   }

   private void buildOmahaUI() {
      memoryEstimates = null;
      JPanel flopRow;
      (flopRow = new JPanel(new FlowLayout(0))).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder flopBorder = BorderFactory.createTitledBorder("Flop");
      flopBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      flopRow.setBorder(flopBorder);

      JPanel flopStrengthPanel;
      (flopStrengthPanel = new JPanel()).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder strengthBorder = BorderFactory.createTitledBorder("Strength");
      strengthBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      flopStrengthPanel.setBorder(strengthBorder);

      JTextField flopBucketsField;
      (flopBucketsField = new JTextField(solver.HashUtil.decodeBy22(new char[0]) + FlopNE.flopBuckets)).setColumns(2);
      flopBucketsField.getDocument().addDocumentListener(new FlopBucketListener(this, flopBucketsField));
      flopRow.add(this.flopPanel);
      flopStrengthPanel.add(new JLabel("Buckets: "));
      flopStrengthPanel.add(flopBucketsField);
      flopRow.add(flopStrengthPanel);

      (flopStrengthPanel = new JPanel()).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder textureBorder = BorderFactory.createTitledBorder("Texture");
      textureBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      flopStrengthPanel.setBorder(textureBorder);
      JRadioButton flopPerfectRadio = new JRadioButton("Perfect");
      flopStrengthPanel.add(flopPerfectRadio);
      (new ButtonGroup()).add(flopPerfectRadio);
      flopPerfectRadio.setSelected(true);
      flopRow.add(flopStrengthPanel);

      (flopStrengthPanel = new JPanel(new FlowLayout(0))).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder turnBorder = BorderFactory.createTitledBorder("Turn");
      turnBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      flopStrengthPanel.setBorder(turnBorder);

      JPanel turnStrengthPanel;
      (turnStrengthPanel = new JPanel()).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder turnStrengthBorder = BorderFactory.createTitledBorder("Strength");
      turnStrengthBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      turnStrengthPanel.setBorder(turnStrengthBorder);

      JTextField turnBucketsField;
      (turnBucketsField = new JTextField(solver.HashUtil.decodeBy28(new char[0]) + FlopNE.turnBuckets)).getDocument().addDocumentListener(new TurnBucketsListener(this, turnBucketsField));
      turnBucketsField.setColumns(2);
      turnStrengthPanel.add(new JLabel("Buckets: "));
      turnStrengthPanel.add(turnBucketsField);

      JPanel turnTexturePanel;
      (turnTexturePanel = new JPanel()).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder turnTextureBorder = BorderFactory.createTitledBorder("Texture");
      turnTextureBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      turnTexturePanel.setBorder(turnTextureBorder);
      JRadioButton turnNoneRadio = new JRadioButton("None");
      JRadioButton turnSmallRadio = new JRadioButton("Small");
      JRadioButton turnMediumRadio = new JRadioButton("Medium");
      JRadioButton turnLargeRadio = new JRadioButton("Large");
      JRadioButton turnPerfectRadio = new JRadioButton("Perfect");
      turnNoneRadio.addItemListener((event) -> {
         if (event.getStateChange() == 1) {
            FlopNE.turnTextureType = 0;
            this.recalculateAsync(false);
         }

      });
      turnSmallRadio.addItemListener((event) -> {
         if (event.getStateChange() == 1) {
            FlopNE.turnTextureType = 3;
            this.recalculateAsync(false);
         }

      });
      turnMediumRadio.addItemListener((event) -> {
         if (event.getStateChange() == 1) {
            FlopNE.turnTextureType = 4;
            this.recalculateAsync(false);
         }

      });
      turnLargeRadio.addItemListener((event) -> {
         if (event.getStateChange() == 1) {
            FlopNE.turnTextureType = 1;
            this.recalculateAsync(false);
         }

      });
      turnPerfectRadio.addItemListener((event) -> {
         if (event.getStateChange() == 1) {
            FlopNE.turnTextureType = 2;
            this.recalculateAsync(false);
         }

      });
      ButtonGroup turnTextureGroup;
      (turnTextureGroup = new ButtonGroup()).add(turnNoneRadio);
      turnTextureGroup.add(turnSmallRadio);
      turnTextureGroup.add(turnMediumRadio);
      turnTextureGroup.add(turnLargeRadio);
      turnTextureGroup.add(turnPerfectRadio);
      turnTexturePanel.add(turnNoneRadio);
      turnTexturePanel.add(turnSmallRadio);
      turnTexturePanel.add(turnMediumRadio);
      turnTexturePanel.add(turnLargeRadio);
      turnTexturePanel.add(turnPerfectRadio);
      if (FlopNE.turnTextureType == 0) {
         turnNoneRadio.setSelected(true);
      } else if (FlopNE.turnTextureType == 1) {
         turnLargeRadio.setSelected(true);
      } else if (FlopNE.turnTextureType == 3) {
         turnSmallRadio.setSelected(true);
      } else if (FlopNE.turnTextureType == 4) {
         turnMediumRadio.setSelected(true);
      } else {
         turnPerfectRadio.setSelected(true);
      }

      flopStrengthPanel.add(this.turnPanel);
      flopStrengthPanel.add(turnStrengthPanel);
      flopStrengthPanel.add(turnTexturePanel);

      (turnStrengthPanel = new JPanel(new FlowLayout(0))).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder riverBorder = BorderFactory.createTitledBorder("River");
      riverBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      turnStrengthPanel.setBorder(riverBorder);

      (turnTexturePanel = new JPanel()).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder riverStrengthBorder = BorderFactory.createTitledBorder("Strength");
      riverStrengthBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      turnTexturePanel.setBorder(riverStrengthBorder);

      JTextField riverBucketsField;
      (riverBucketsField = new JTextField(solver.HashUtil.decodeBy38(new char[0]) + FlopNE.riverBuckets)).getDocument().addDocumentListener(new RiverBucketsListener(this, riverBucketsField));
      riverBucketsField.setColumns(2);
      turnTexturePanel.add(new JLabel("Buckets: "));
      turnTexturePanel.add(riverBucketsField);

      JPanel riverTexturePanel;
      (riverTexturePanel = new JPanel()).setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder riverTextureBorder = BorderFactory.createTitledBorder("Texture");
      riverTextureBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      riverTexturePanel.setBorder(riverTextureBorder);
      (turnSmallRadio = new JRadioButton("None")).addItemListener(new RiverTextureNoneListener(this));
      (turnMediumRadio = new JRadioButton("Small")).addItemListener(new RiverTexturePerfectListener(this));
      (turnLargeRadio = new JRadioButton("Large")).addItemListener(new RiverTextureLargeListener(this));
      ButtonGroup riverTextureGroup;
      (riverTextureGroup = new ButtonGroup()).add(turnSmallRadio);
      riverTextureGroup.add(turnMediumRadio);
      riverTextureGroup.add(turnLargeRadio);
      riverTexturePanel.add(turnSmallRadio);
      riverTexturePanel.add(turnMediumRadio);
      riverTexturePanel.add(turnLargeRadio);
      if (FlopNE.riverTextureType == 0) {
         turnSmallRadio.setSelected(true);
      } else if (FlopNE.riverTextureType == 2) {
         turnMediumRadio.setSelected(true);
      } else {
         turnLargeRadio.setSelected(true);
      }

      turnStrengthPanel.add(this.riverPanel);
      turnStrengthPanel.add(turnTexturePanel);
      turnStrengthPanel.add(riverTexturePanel);
      this.flopPanel.setLayout(new BoxLayout(this.flopPanel, 1));
      this.turnPanel.setLayout(new BoxLayout(this.turnPanel, 1));
      this.riverPanel.setLayout(new BoxLayout(this.riverPanel, 1));
      Dimension summarySize = new Dimension((int)(150.0F * PokerSolverMain.c), (int)(60.0F * PokerSolverMain.c));
      this.flopPanel.setPreferredSize(summarySize);
      this.riverPanel.setPreferredSize(summarySize);
      this.turnPanel.setPreferredSize(summarySize);
      this.add(flopRow);
      this.add(flopStrengthPanel);
      this.add(turnStrengthPanel);
      this.recalculateAsync(false);
   }
   
   //Hold'em

   private void buildHoldemUI() {
      JRadioButton smallRiverSuitRadio;
      (smallRiverSuitRadio = new JRadioButton("Small")).addActionListener((event) -> {
         GameSettings.c = 0;
      });
      JRadioButton largeRiverSuitRadio;
      (largeRiverSuitRadio = new JRadioButton("Large")).addActionListener((event) -> {
         GameSettings.c = 1;
      });
      JPanel topPanel;
      if (!AnalysisPanel.isHoldem()) {
         topPanel = new JPanel();
         JButton estimateRamBtn;
         (estimateRamBtn = new JButton("Estimate RAM")).addActionListener((event) -> {
            estimateRamBtn.setIcon(MainTabbedPane.runIcon);
            (new Thread(() -> {
               this.recalculateAsync(true);
               estimateRamBtn.setIcon((Icon)null);
            })).start();
         });
         topPanel.add(estimateRamBtn);
         String customAbstractionDefault = "Custom abstraction";
         JButton customAbstractionBtn;
         (customAbstractionBtn = new JButton(solver.HashUtil.decodeBy48(new char[0]))).setText(GameSettings.d == null ? customAbstractionDefault : GameSettings.d.getName());
         customAbstractionBtn.addActionListener((event) -> {
            JFileChooser fileChooser;
            (fileChooser = new JFileChooser()).setCurrentDirectory(new util.AppFile("Views"));
            fileChooser.setDialogTitle("Select view file");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", new String[]{"txt"}));
            if (fileChooser.showOpenDialog(MainTabbedPane.k) == 0) {
               File selectedFile;
               if ((selectedFile = fileChooser.getSelectedFile()) != null && selectedFile.exists()) {
                  GameSettings.d = new ViewSettingsManager(selectedFile);
               } else {
                  GameSettings.d = null;
               }
            } else {
               GameSettings.d = null;
            }

            if (GameSettings.d == null) {
               customAbstractionBtn.setText(customAbstractionDefault);
            } else {
               customAbstractionBtn.setText(GameSettings.d.getName());
            }
         });
         if (AnalysisPanel.gameType == 1) {
            topPanel.add(customAbstractionBtn);
         }

         this.add(topPanel);
      }

      if (memoryEstimates == null) {
         memoryEstimates = new long[4];
         this.titledBorder.setTitle("Abstraction");
      }

      (topPanel = new JPanel(new FlowLayout(0))).setMaximumSize(new Dimension(1231231, 120));
      topPanel.setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder flopBorder = BorderFactory.createTitledBorder("Flop");
      flopBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      topPanel.setBorder(flopBorder);
      topPanel.add(this.buildFlopSummary());
      JPanel turnRow;
      (turnRow = new JPanel(new FlowLayout(0))).setMaximumSize(new Dimension(1231231, 120));
      turnRow.setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder turnBorder = BorderFactory.createTitledBorder("Turn");
      turnBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      turnRow.setBorder(turnBorder);
      turnRow.add(this.buildTurnSummary());
      JPanel riverRow;
      (riverRow = new JPanel(new FlowLayout(0))).setMaximumSize(new Dimension(1231231, 120));
      riverRow.setBackground(ThemeManager.BACKGROUND_DARK);
      javax.swing.border.TitledBorder riverBorder = BorderFactory.createTitledBorder("River");
      riverBorder.setTitleColor(ThemeManager.TEXT_PRIMARY);
      riverRow.setBorder(riverBorder);
      riverRow.add(this.buildRiverSummary());
      JPanel suitRadiosPanel = new JPanel();
      smallRiverSuitRadio.setToolTipText("Only suits with 3 or more cards on the river are considered.");
      largeRiverSuitRadio.setToolTipText("Suits which had at least 2 cards by the turn are considered.");
      if (GameSettings.c == 0) {
         smallRiverSuitRadio.setSelected(true);
      } else {
         largeRiverSuitRadio.setSelected(true);
      }

      ButtonGroup riverSuitGroup;
      (riverSuitGroup = new ButtonGroup()).add(smallRiverSuitRadio);
      riverSuitGroup.add(largeRiverSuitRadio);
      suitRadiosPanel.add(smallRiverSuitRadio);
      suitRadiosPanel.add(largeRiverSuitRadio);
      if (!AnalysisPanel.isHoldem()) {
         riverRow.add(suitRadiosPanel);
      }

      this.add(topPanel);
      this.add(turnRow);
      this.add(riverRow);
      if ( (AnalysisPanel.gameType == 0) || (AnalysisPanel.gameType == 3) ) {
         this.recalculateAsync(false);
      }

      this.revalidate();
      this.repaint();
   }

   private static /* synthetic */ void computeBucketsForPlayerAsync(boolean isOmaha, int playerIdx, GameState gameState, card[] boardCards, long[][] perPlayerCounts) {
        try {
            int[] streetCounts = isOmaha ? FlopNE.computePostflopBucketsForOmahaRange((ViewSettingsManager)GameSettings.d, (OmahaHandRange)((OmahaHandRange)AnalysisPanel.getRangeForPlayer((int)playerIdx)), (int)gameState.gameStage, (card[])boardCards, (int)GameSettings.c) :
                    FlopNE.computePostflopBucketsHoldem((card[][])AnalysisPanel.getRangeForPlayer((int)playerIdx).getAllCards(), (int)gameState.gameStage, (card[])boardCards, (collections.LongIntHashMap)new collections.LongIntHashMap());
            perPlayerCounts[playerIdx][1] = streetCounts[0];
            perPlayerCounts[playerIdx][2] = streetCounts[1];
            perPlayerCounts[playerIdx][3] = streetCounts[2];
            long[] estimatesLock = memoryEstimates;
            synchronized (estimatesLock) {
                long[] flopEstimates = memoryEstimates;
                flopEstimates[1] = flopEstimates[1] + (long)(streetCounts[0] / gameState.nWay);
                long[] turnEstimates = memoryEstimates;
                turnEstimates[2] = turnEstimates[2] + (long)(streetCounts[1] / gameState.nWay);
                long[] riverEstimates = memoryEstimates;
                riverEstimates[3] = riverEstimates[3] + (long)(streetCounts[2] / gameState.nWay);
                return;
            }
        }
        catch (InterruptedException interruptedException) {
            return;
        }
    }
   
}
