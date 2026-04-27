package solver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public final class GameTree extends JTree {
   ArrayList filteredNodes = new ArrayList();
   PokerTreeNode lastFilteredRoot;
   FilterSettings lastFilterSettings;
   JButton undoButton = new JButton("Undo");
   JButton redoButton;

   public GameTree(PokerTreeNode root) {
      super(root);

      this.lastFilteredRoot = root;

      this.undoButton.addActionListener((event) -> {
         this.undoFilter();
      });
      this.redoButton = new JButton("Redo");
      this.redoButton.addActionListener((event) -> {
         this.redoFilter();
      });
      this.undoButton.setEnabled(false);
      this.redoButton.setEnabled(false);
      AnalysisPanel.l = Math.round((float)this.getFont().getSize() * 1.8F);
      this.setRowHeight(AnalysisPanel.l);
      JPopupMenu popupMenu = new JPopupMenu();
      JMenuItem removeItem;
      (removeItem = new JMenuItem("Remove")).addActionListener(new SaveRangeListener(this));
      JMenuItem removeAllItem;
      (removeAllItem = new JMenuItem("Remove all")).addActionListener((event) -> {
         if (AnalysisPanel.selectedNode != null) {
            ArrayList betTypes = new ArrayList(AnalysisPanel.INSTANCE.betTypes);
            if (AnalysisPanel.INSTANCE.customBetCheckBox.isSelected() && AnalysisPanel.INSTANCE.customBet.intValue() > 0) {
               betTypes.add(AnalysisPanel.INSTANCE.customBet);
            }

            AnalysisPanel.selectedNode.removeMatchingNodes(betTypes);
         }

      });
      JMenu addToAllMenu = new JMenu("Add to all");
      JMenu savedFiltersMenu = new JMenu("Saved filters");
      JMenuItem customFilterItem;
      (customFilterItem = new JMenuItem("Custom filter")).addActionListener((event) -> {
         AnalysisPanel.p = new FilterSettings();
         this.showFilterDialog();
      });
      JMenuItem postflopNodesItem;
      (postflopNodesItem = new JMenuItem("Postflop nodes")).addActionListener((event) -> {
         if (AnalysisPanel.selectedNode != null) {
            AnalysisPanel.q.p = new ArrayList(AnalysisPanel.INSTANCE.betTypes);
            if (AnalysisPanel.INSTANCE.customBetCheckBox.isSelected() && AnalysisPanel.INSTANCE.customBet.intValue() > 0) {
               AnalysisPanel.q.p.add(AnalysisPanel.INSTANCE.customBet);
            }

            AnalysisPanel.q.a = false;
            AnalysisPanel.q.b = true;
            this.applyFilter(AnalysisPanel.selectedNode, AnalysisPanel.q);
         }

      });
      JMenuItem postflopNoDonkItem;
      (postflopNoDonkItem = new JMenuItem("Postflop nodes w/o donk")).addActionListener((event) -> {
         if (AnalysisPanel.selectedNode != null) {
            AnalysisPanel.q.p = new ArrayList(AnalysisPanel.INSTANCE.betTypes);
            if (AnalysisPanel.INSTANCE.customBetCheckBox.isSelected() && AnalysisPanel.INSTANCE.customBet.intValue() > 0) {
               AnalysisPanel.q.p.add(AnalysisPanel.INSTANCE.customBet);
            }

            AnalysisPanel.q.a = true;
            this.applyFilter(AnalysisPanel.selectedNode, AnalysisPanel.q);
         }

      });
      JMenuItem preflopNodesItem;
      (preflopNodesItem = new JMenuItem("Preflop nodes")).addActionListener(new ClearRangeListener(this));
      addToAllMenu.add(preflopNodesItem);
      addToAllMenu.add(postflopNodesItem);
      addToAllMenu.add(postflopNoDonkItem);
      addToAllMenu.addSeparator();
      addToAllMenu.add(customFilterItem);
      util.AppFile filtersDir = new util.AppFile("Filters");
      popupMenu.addPopupMenuListener(new SelectAllRangeListener(this, addToAllMenu, preflopNodesItem, postflopNodesItem, postflopNoDonkItem, customFilterItem, filtersDir, savedFiltersMenu));
      popupMenu.add(addToAllMenu);
      JMenuItem editItem;
      (editItem = new JMenuItem("Edit")).addActionListener(new DeselectAllRangeListener(this));
      popupMenu.add(editItem);
      popupMenu.add(removeItem);
      popupMenu.add(removeAllItem);
      (new JMenuItem("Expand all")).addActionListener((event) -> {
         EventQueue.invokeLater(() -> {
            this.expandOrCollapseTree(AnalysisPanel.selectedNode, true);
         });
      });
      (new JMenuItem("Collapse all")).addActionListener((event) -> {
         EventQueue.invokeLater(() -> {
            this.expandOrCollapseTree(AnalysisPanel.selectedNode, false);
         });
      });
      InvertRangeListener invertListener = new InvertRangeListener(this, popupMenu);
      this.setCellRenderer(AnalysisPanel.treeCellRenderer);
      this.addTreeSelectionListener(AnalysisPanel.treeSelectionHandler);
      this.addMouseListener(invertListener);
      this.setUI(new PokerTree$6(this));
      this.addKeyListener(new CopyRangeListener(this));
      this.getInputMap(1).clear();
      this.getInputMap().clear();
   }

   private synchronized void undoFilter() {
      Iterator iterator = this.filteredNodes.iterator();

      while(iterator.hasNext()) {
         ((PokerTreeNode)iterator.next()).cleanupAndRemove();
      }

      this.filteredNodes.clear();
      this.redoButton.setEnabled(true);
      this.undoButton.setEnabled(false);
   }

   private synchronized void redoFilter() {
      if (this.lastFilteredRoot != null && this.lastFilterSettings != null) {
         this.applyFilter(this.lastFilteredRoot, this.lastFilterSettings);
      }

      this.redoButton.setEnabled(false);
   }

   public static void addBetNodes(PokerTreeNode parent, ArrayList betTypes) {
      // This one for just adding one node
      Iterator iterator = betTypes.iterator();

      while(iterator.hasNext()) {
         BetType betType;
         if ((betType = (BetType)iterator.next()).intValue() == -1) {
            parent.createChildNode(solver.BetType.suggest(parent.gameState).intValue(), false);
         } else {
            parent.createChildNode(betType.intValue(), false);
         }
      }

      if (AnalysisPanel.INSTANCE.customBetCheckBox.isSelected() && AnalysisPanel.INSTANCE.customBet.intValue() > 0) {
         parent.createChildNode(AnalysisPanel.INSTANCE.customBet.intValue(), false);
      }

   }

   public static void removeNode(PokerTreeNode node) {
      node.removeFromParent();
   }

   private void applyFilter(PokerTreeNode root, FilterSettings settings) {
      this.filteredNodes.clear();
      root.applyFilterSettings(settings, this.filteredNodes);
      this.lastFilterSettings = new FilterSettings(settings);
      this.lastFilteredRoot = root;
      this.undoButton.setEnabled(true);
      this.redoButton.setEnabled(false);
   }

   private void showFilterDialog() {
      if (AnalysisPanel.selectedNode != null) {
         JDialog dialog = new JDialog(MainTabbedPane.k, false);
         JPanel contentPanel;
         (contentPanel = new JPanel()).setLayout(new BorderLayout());
         JPanel filterPanel = AnalysisPanel.buildFilterPanel(dialog, new JPanel(), AnalysisPanel.p, true);
         contentPanel.add(filterPanel, "Center");
         JButton applyButton;
         (applyButton = new JButton("Apply")).addActionListener((event) -> {
            this.applyFilter(AnalysisPanel.selectedNode, AnalysisPanel.p);
            dialog.setVisible(false);
            dialog.dispose();
         });
         JButton cancelButton;
         (cancelButton = new JButton("Cancel")).addActionListener((event) -> {
            dialog.setVisible(false);
            dialog.dispose();
         });
         JButton saveButton;
         (saveButton = new JButton("Save")).addActionListener((event) -> {
            String filterName;
            if ((filterName = JOptionPane.showInputDialog((Component)null, solver.HashUtil.decodeBy18(new char[0]), "Save as", -1)) != null) {
               String nameToSave = filterName;
               FilterSettings currentFilter = AnalysisPanel.p;

               try {
                  Equity.writeObject((File)(new util.AppFile("Filters")), (String)nameToSave, (Object)currentFilter.toProperties());
                  return;
               } catch (Exception saveEx) {
                  saveEx.printStackTrace();
               } catch (Throwable ex) {
                  Logger.getLogger(GameTree.class.getName()).log(Level.SEVERE, null, ex);
               }
            }

         });
         filterPanel.setBorder(new EmptyBorder(0, 0, 18, 0));
         (filterPanel = new JPanel()).add(applyButton);
         filterPanel.add(cancelButton);
         filterPanel.add(saveButton);
         contentPanel.add(filterPanel, "South");
         dialog.setContentPane(contentPanel);
         dialog.pack();
         dialog.setVisible(true);
      }

   }

   private void populateFilterMenu(JMenu menu, File dir) {
      File[] entries;
      int entryCount = (entries = dir.listFiles()).length;

      for(int i = 0; i < entryCount; ++i) {
         if ((dir = entries[i]).isDirectory()) {
            JMenu subMenu = new JMenu(dir.getName());
            this.populateFilterMenu(subMenu, dir);
         } else {
            JMenuItem item = new JMenuItem(dir.getName());
            menu.add(item);
            File filterFile = dir;
            item.addActionListener((event) -> {
               AnalysisPanel.p = FilterSettings.fromProperties((Properties)Equity.readObject(filterFile));
               this.showFilterDialog();
            });
         }
      }

   }

   public final GameState getRootGameState() {
      return ((PokerTreeNode)this.getModel().getRoot()).gameState;
   }

   public final int getRootNWay() {
      return ((PokerTreeNode)this.getModel().getRoot()).gameState.nWay;
   }

   public final void reloadModel() {
      ((DefaultTreeModel)this.getModel()).reload();
   }

   private void expandOrCollapseTree(PokerTreeNode root, boolean expand) {
      TreeExpansionListener[] savedListeners;
      TreeExpansionListener[] listenersToRemove;
      int removeCount = (listenersToRemove = savedListeners = this.getTreeExpansionListeners()).length;

      for(int i = 0; i < removeCount; ++i) {
         TreeExpansionListener listener = listenersToRemove[i];
         this.removeTreeExpansionListener(listener);
      }

      TreePath rootPath = new TreePath(root.getPath());
      expandOrCollapseRecursive(this, rootPath, expand);
      TreeExpansionListener[] listenersToReadd = savedListeners;
      int readdCount = savedListeners.length;

      for(removeCount = 0; removeCount < readdCount; ++removeCount) {
         TreeExpansionListener listener = listenersToReadd[removeCount];
         this.addTreeExpansionListener(listener);
      }

      this.collapsePath(rootPath);
      if (expand) {
         this.expandPath(rootPath);
      }

   }

   private static void expandOrCollapseRecursive(JTree tree, TreePath path, boolean expand) {
      TreeModel model = tree.getModel();
      Object lastNode = path.getLastPathComponent();
      int childCount;
      if ((childCount = model.getChildCount(lastNode)) != 0) {
         if (expand) {
            tree.expandPath(path);
         } else {
            tree.collapsePath(path);
         }

         for(int i = 0; i < childCount; ++i) {
            Object child = model.getChild(lastNode, i);
            if (model.getChildCount(child) > 0) {
               LoadRangeListener childPath = new LoadRangeListener(path, child);
               expandOrCollapseRecursive(tree, childPath, expand);
            }
         }

      }
   }

   private static void writeInt32(BufferedWriter writer, int value) throws IOException {
      writer.write(value >> 16);
      writer.write(value & '\uffff');
   }

   private static int readInt32(BufferedReader reader) throws IOException {
      int highBits = reader.read() << 16;
      return reader.read() | highBits;
   }

   public final void writeToFile(BufferedWriter writer, boolean writeEquity) throws IOException {
      PokerTreeNode root = (PokerTreeNode)this.getModel().getRoot();
      writer.write(33485);
      writer.write(root.gameState.nWay);
      writer.write(root.gameState.firstPlayerToAct);
      writer.write(root.gameState.gameStage);
      int player;
      if (root.gameState.gameStage == 0) {
         for(player = 0; player < root.gameState.nWay; ++player) {
            writeInt32(writer, root.gameState.bets[player]);
         }
      }

      writeInt32(writer, root.gameState.deadMoney[0]);

      for(player = 0; player < root.gameState.nWay; ++player) {
         writer.write(root.gameState.stacks[player] / 1000);
      }

      this.writeNode(writer, root);

      if (writeEquity) {
         for(player = 0; player < root.gameState.nWay; ++player) {
            double[] equityArray = AnalysisPanel.getRangeForPlayer(player).weights;

            for(int handIdx = 0; handIdx < equityArray.length; ++handIdx) {
               int roundedEquityValue = 0;

               if ((equityArray[handIdx] >= 0.84D) && (equityArray[handIdx] < 0.855D) ) {
                  roundedEquityValue = (int)Math.round(0.84D * 65535.0D);
               }
               if ((equityArray[handIdx] >= 0.855D) && (equityArray[handIdx] <= 0.88D) ) {
                  roundedEquityValue = (int)Math.round(0.88D * 65535.0D);
               }

            	/*if (var9[var5] == 0.85D) {
            		roundedEquityValue = (int)Math.round(0.84D * 65535.0D);
            	}
            	if (var9[var5] == 0.858D) {
            		roundedEquityValue = (int)Math.round(0.84D * 65535.0D);
            	}
            	if (var9[var5] == 0.86D) {
            		roundedEquityValue = (int)Math.round(0.84D * 65535.0D);
            	}
            	if (var9[var5] == 0.866D) {
            		roundedEquityValue = (int)Math.round(0.88D * 65535.0D);
            	}
            	if (var9[var5] == 0.87D) {
            		roundedEquityValue = (int)Math.round(0.88D * 65535.0D);
            	}*/

               if (roundedEquityValue == 0) {
                  roundedEquityValue = (int)Math.round(equityArray[handIdx] * 65535.0D);
               }

               char valueChar = (char)(roundedEquityValue);
               writer.write(valueChar + "");
            }
         }
      }

   }

   private void writeNode(BufferedWriter writer, PokerTreeNode node) throws IOException {
      Enumeration children = node.children();
      if (node.nodeType >= 0) {
         writer.write(node.nodeType);
      }

      writer.write(node.answers.length);

      while(children.hasMoreElements()) {
         this.writeNode(writer, (PokerTreeNode)children.nextElement());
      }

   }

   public static GameState loadGameStateFromArchive(File file) throws Throwable {
      if (file.getName().endsWith(".mkr") && !file.isDirectory()) {
         try {
            Throwable firstError = null;

            try {
               ZipFile zip = new ZipFile(file, StandardCharsets.UTF_16);

               GameState result;
               try {
                  result = loadGameStateFromStream(zip.getInputStream(zip.getEntry("tree")));
               } finally {
                  zip.close();
               }

               return result;
            } catch (Throwable ex) {
               if (firstError == null) {
                  firstError = ex;
               } else if (firstError != ex) {
                  firstError.addSuppressed(ex);
               }

               throw firstError;
            }
         } catch (Exception outerEx) {
            outerEx.printStackTrace();
            return null;
         }
      } else {
         return loadGameStateFromFile(new File(file, "tree"));
      }
   }

   private static GameState readNodeFromFile(BufferedReader reader) throws IOException {
      int nWay = reader.read();
      byte version = 0;
      if (nWay > 120) {
         version = 1;
         if (nWay == 33482) {
            version = 2;
         } else if (nWay == 33483) {
            version = 3;
         } else if (nWay == 33484) {
            version = 4;
         } else if (nWay == 33485) {
            version = 5;
         }

         nWay = reader.read();
      }

      int firstPlayerToAct = -1;
      if (version >= 5) {
         firstPlayerToAct = reader.read();
      }

      int gameStage = reader.read();
      int[] bets = null;
      int idx;
      if (gameStage == 0 && version >= 3) {
         ArrayList betsList = new ArrayList();

         int i;
         for(i = 0; i < nWay; ++i) {
            if (version >= 4) {
               idx = readInt32(reader);
            } else {
               idx = reader.read();
            }

            if (idx > 0) {
               betsList.add(idx);
            }
         }

         Collections.sort(betsList);
         bets = new int[betsList.size()];

         for(i = 0; i < bets.length; ++i) {
            bets[i] = (Integer)betsList.get(i) / 100;
         }
      }

      int deadMoney;
      if (version >= 2) {
         deadMoney = readInt32(reader);
      } else {
         deadMoney = reader.read();
      }

      if (version == 0) {
         deadMoney *= 1000;
      }

      GameState state =
              bets == null ?
                      new GameState(nWay, gameStage, deadMoney, 0, -1) :
                      new GameState(bets, nWay, gameStage, deadMoney, firstPlayerToAct, 0, -1);

      for(idx = 0; idx < state.nWay; ++idx) {
         state.stacks[idx] = reader.read() * 1000;
      }

      return state;
   }

   private static GameTree readTreeFromFile(BufferedReader inputBuffer) throws IOException {
      int nWay = inputBuffer.read();
      byte version = 0;
      if (nWay > 120) {
         version = 1;
         if (nWay == 33482) {
            version = 2;
         } else if (nWay == 33483) {
            version = 3;
         } else if (nWay == 33484) {
            version = 4;
         } else if (nWay == 33485) {
            version = 5;
         }

         nWay = inputBuffer.read();
      }

      int firstPlayerToAct = -1;
      if (version >= 5) {
         firstPlayerToAct = inputBuffer.read();
      }

      int gameStage = inputBuffer.read();
      int[] bets = null;
      int i;
      int player;
      if (gameStage == 0 && version >= 3) {
         ArrayList betsList = new ArrayList();

         for(i = 0; i < nWay; ++i) {
            if (version >= 4) {
               player = readInt32(inputBuffer);
            } else {
               player = inputBuffer.read();
            }

            betsList.add(player);
         }

         if (nWay > 2) {
            i = (Integer)betsList.get(0);
            betsList.remove(0);
            betsList.add(i);
         }

         bets = new int[betsList.size()];

         for(i = 0; i < bets.length; ++i) {
            bets[i] = (Integer)betsList.get(i) / 100;
         }
      }

      int deadMoney;
      if (version >= 2) {
         deadMoney = readInt32(inputBuffer);
      } else {
         deadMoney = inputBuffer.read();
      }

      if (version == 0) {
         deadMoney *= 1000;
      }

      GameState rootState =
              bets == null ?
                      new GameState(nWay, gameStage, deadMoney, 0, -1) :
                      new GameState(bets, nWay, gameStage, deadMoney, firstPlayerToAct, 0, -1);

      for(player = 0; player < rootState.nWay; ++player) {
         rootState.stacks[player] = inputBuffer.read() * 1000;
      }

      PokerTreeNode.nodeCache.clear();
      PokerTreeNode rootNode = new PokerTreeNode(rootState);

      GameTree tree = new GameTree(rootNode);
      DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
      readChildNodes(treeModel, inputBuffer, rootNode, inputBuffer.read());

      AnalysisPanel.initRanges(nWay, gameStage);

      int holeCards = AnalysisPanel.isHoldem() ? 1326 : 270725;
      //int holeCards = AnalysisPanel.isHoldem() ? 630 : 270725;

      if (holeCards == 630) {
         for(player = 0; player < nWay; ++player) {
            for(int handIdx = 0; handIdx < 1326; ++handIdx) {
               AnalysisPanel.getRangeForPlayer(player).weights[handIdx] = 0.0D;
            }
         }
      }


      label81:
      for(player = 0; player < nWay; ++player) {
         for(int handIdx = 0; handIdx < holeCards; ++handIdx) {
            if ((i = inputBuffer.read()) < 0) {
               System.lineSeparator();
               break label81;
            }
            AnalysisPanel.getRangeForPlayer(player).weights[handIdx] = 0.0D;

            if (holeCards == 630) {
               int card1 = AnalysisPanel.getRangeForPlayer(player).COMBO_CARD1[handIdx];
               int card2 = AnalysisPanel.getRangeForPlayer(player).COMBO_CARD2[handIdx];

               int card1FD = ((card1 % 9) + 4) + (card1 / 9)*13;
               int card2FD = ((card2 % 9) + 4) + (card2 / 9)*13;

               int k = 0;
               while(k < 1326) {
                  if ( (AnalysisPanel.getRangeForPlayer(player).COMBO_CARD1[k] == card1FD) && (AnalysisPanel.getRangeForPlayer(player).COMBO_CARD2[k] == card2FD) ) {
                     break;
                  }

                  k++;
               }

               if (k < 1326) {
                  AnalysisPanel.getRangeForPlayer(player).weights[k] = (double)i / 65535.0D;
               }
            } else {
               AnalysisPanel.getRangeForPlayer(player).weights[handIdx] = (double)i / 65535.0D;
            }
         }
      }

      treeModel.reload();
      return tree;
   }

   private static GameState loadGameStateFromStream(InputStream stream) throws Throwable {
      try {
         Throwable firstError = null;

         try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_16));

            GameState result;
            try {
               result = readNodeFromFile(reader);
            } finally {
               reader.close();
            }

            return result;
         } catch (Throwable ex) {
            if (firstError == null) {
               firstError = ex;
            } else if (firstError != ex) {
               firstError.addSuppressed(ex);
            }

            throw firstError;
         }
      } catch (Exception outerEx) {
         Exception capturedEx = outerEx;
         File errorLogFile = new File("test2.log");

         try {
            PrintStream errorPrintStream = new PrintStream(errorLogFile);
            capturedEx.printStackTrace(errorPrintStream);
         } catch (FileNotFoundException ignored) {
         }

         return null;
      }
   }

   public static GameState loadGameStateFromFile(File file) throws Throwable {
      try {
         Throwable firstError = null;

         try {
            BufferedReader reader = Files.newBufferedReader(file.toPath());

            GameState result;
            try {
               result = readNodeFromFile(reader);
            } finally {
               if (reader != null) {
                  reader.close();
               }

            }

            return result;
         } catch (Throwable ex) {
            if (firstError == null) {
               firstError = ex;
            } else if (firstError != ex) {
               firstError.addSuppressed(ex);
            }

            throw firstError;
         }
      } catch (Exception outerEx) {
         return null;
      }
   }

   public static GameTree loadTreeFromStream(InputStream stream) throws Throwable {
      try {
         Throwable firstError = null;

         try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_16));

            GameTree result;
            try {
               result = readTreeFromFile(reader);
            } finally {
               reader.close();
            }

            return result;
         } catch (Throwable ex) {
            if (firstError == null) {
               firstError = ex;
            } else if (firstError != ex) {
               firstError.addSuppressed(ex);
            }

            throw firstError;
         }
      } catch (Exception outerEx) {
         return null;
      }
   }

   public static GameTree loadTreeFromFile(File file) throws Throwable {
      try {
         Throwable firstError = null;

         try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

            GameTree result;
            try {
               result = readTreeFromFile(reader);
            } finally {
               reader.close();
            }

            return result;
         } catch (Throwable ex) {
            if (firstError == null) {
               firstError = ex;
            } else if (firstError != ex) {
               firstError.addSuppressed(ex);
            }

            throw firstError;
         }
      } catch (Exception outerEx) {
         outerEx.printStackTrace();
         return null;
      }
   }

   private static void readChildNodes(DefaultTreeModel model, BufferedReader reader, PokerTreeNode parent, int childCount) throws IOException {
      parent.registerInCache();

      for(int childIdx = 0; childIdx < childCount; ++childIdx) {
         int nodeType = reader.read();
         PokerTreeNode child = new PokerTreeNode(parent, nodeType, false);
         readChildNodes(model, reader, child, reader.read());
         int[] newAnswers;
         (newAnswers = new int[parent.answers.length + 1])[0] = nodeType;

         for(nodeType = 0; nodeType < parent.answers.length; ++nodeType) {
            newAnswers[nodeType + 1] = parent.answers[nodeType];
         }

         parent.answers = newAnswers;
         model.insertNodeInto(child, parent, childIdx);
      }

   }

   static void createAdvancedTree(DefaultTreeModel treeModel, PokerTreeNode tree, AdvTreeSituation state) {
/*
	betType - 40000
		= % from POT
	betType - 11
		= X chips
	0	= FOLD
	1	= CHECK / CALL
	2	= POT
	3	= ALLIN
	4	= 1/2 POT
	5	= MIN RAISE
	6	= BET
	7	= 1/4 POT
	8	= 2 POT
	9	= 3/4 POT


	0 - FOLD
	1 - CHECK/CALL
	3 - ALLIN
	11+X - X chips

	[1..39989]	chips
*/
      GameState treeSettings = tree.gameState;

      if (treeSettings.gameStage == 4) {
         return;
      }

      if (treeSettings.parentNode != null) {
         if (treeSettings.gameStage > treeSettings.parentNode.gameStage) {
            state.getPostflopType();
         }
      }

      List<Double> children = AdvTreeSettings.getPossibleAnswers(treeSettings, state);
      int childrenCount = children.size();

      for(int childIdx = 0; childIdx < childrenCount; ++childIdx) {
         double childValue = children.get(childIdx);
         int childValueInt = (int)childValue;

         PokerTreeNode childNode = new PokerTreeNode(tree, childValue, false);
         createAdvancedTree(treeModel, childNode, new AdvTreeSituation(state, treeSettings, childValue));
         int[] newAnswers;
         (newAnswers = new int[tree.answers.length + 1])[0] = childValueInt;

         for(int i = 0; i < tree.answers.length; ++i) {
            newAnswers[i + 1] = tree.answers[i];
         }

         tree.answers = newAnswers;
         treeModel.insertNodeInto(childNode, tree, childIdx);
      }
   }

   // $FF: synthetic method
   static void applyFilter(GameTree tree, PokerTreeNode node, FilterSettings settings) {
      tree.applyFilter(node, settings);
   }

   // $FF: synthetic method
   static void populateFilterMenu(GameTree tree, JMenu menu, File dir) {
      tree.populateFilterMenu(menu, dir);
   }
}
