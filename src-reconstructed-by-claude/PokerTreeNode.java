package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public final class PokerTreeNode extends DefaultMutableTreeNode {
   static final String[] STREET_NAMES = new String[]{"PREFLOP", "FLOP", "TURN", "RIVER", "SHOWDOWN"};
   private static final long serialVersionUID = -3926025528308114435L;
   int nodeType = -1;
   private PokerTreeNode parentNode;
   GameState gameState;
   static HashMap nodeCache = new HashMap();
   static int[] betCommittalThresholds = new int[]{41, 51, 61, 71, 90, 90, 90, 90, 35, 45, 55, 65, 90, 90, 90, 90};
   static int[] callCommittalThresholds = new int[]{70, 70, 80, 100, 60, 60, 70, 100, 70, 70, 80, 100, 70, 70, 80, 100};
   static int[] foldCommittalThresholds = new int[]{80, 80, 80, 100, 75, 80, 90, 100, 90, 90, 90, 100, 75, 80, 90, 100};
   static int[] allInThresholds = new int[]{1300, 800, 700, 700, 0, 0, 0, 0, 2600, 1300, 1100, 900, 0, 0, 0, 0};
   int[] answers = new int[0];
   private int descendantCount = -1;
   public static String currentProfileName = "Default";
   private static FilterSettings activeFilterSettings;

   public PokerTreeNode(GameState initialState) {
      super("ROOT");
      this.gameState = initialState;
      this.registerInCache();
   }

   public static final void setBetSizingProfile(String profileName, ArrayList thresholdLists) {
      currentProfileName = profileName;
      allInThresholds = (int[])((int[])thresholdLists.get(0)).clone();
      betCommittalThresholds = (int[])((int[])thresholdLists.get(1)).clone();
      callCommittalThresholds = (int[])((int[])thresholdLists.get(2)).clone();
      foldCommittalThresholds = (int[])((int[])thresholdLists.get(3)).clone();
   }

   public PokerTreeNode getParentNode(){
      return this.parentNode;
   }
   public static final ArrayList getBetSizingData() {
      ArrayList sizingData;
      ArrayList returnRef = sizingData = new ArrayList();
      int[] thresholds = allInThresholds;
      returnRef.add(Arrays.copyOf(thresholds, thresholds.length));
      thresholds = betCommittalThresholds;
      sizingData.add(Arrays.copyOf(thresholds, thresholds.length));
      thresholds = callCommittalThresholds;
      sizingData.add(Arrays.copyOf(thresholds, thresholds.length));
      thresholds = foldCommittalThresholds;
      sizingData.add(Arrays.copyOf(thresholds, thresholds.length));
      return sizingData;
   }

   public PokerTreeNode(PokerTreeNode parent, int betType, boolean expandChildren) {
      super(solver.BetType.format(betType));

      this.gameState = new GameState(parent.gameState, betType);
      if (betType > 80000){
         this.gameState.noAnte = true;
         int prevPlayer = this.gameState.parentNode.lastAggressorIndex;
         int resolvedNodeType = (this.gameState.bets[this.gameState.lastAggressorIndex] - this.gameState.parentNode.getMaxBet())/1000;

         this.gameState.nodeType = 11 + (resolvedNodeType);
         this.nodeType = 11 + resolvedNodeType;
      }else{
         this.nodeType = betType;
      }

      this.parentNode = parent;
      if (expandChildren) {
         this.expandDefaultChildren();
      }

   }

   public PokerTreeNode(PokerTreeNode parent, double betType, boolean expandChildren) {
      super(solver.BetType.format(betType));

      int intBetType = (int)betType;

      this.nodeType = intBetType;
      this.gameState = new GameState(parent.gameState, betType);
      this.parentNode = parent;
      if (expandChildren) {
         this.expandDefaultChildren();
      }

   }

   public final boolean updateNodeType(int newNodeType) {
      if (this.parentNode != null && this.parentNode.gameState != null) {
         if (this.parentNode.gameState.gameStage >= 4 || !this.parentNode.gameState.isActionAllowed(newNodeType)) {
            return false;
         }

         int answerIndex = 0;
         int[] parentAnswers;
         int parentAnswerCount = (parentAnswers = this.parentNode.answers).length;

         for(int i = 0; i < parentAnswerCount && parentAnswers[i] != this.nodeType; ++i) {
            ++answerIndex;
         }

         this.parentNode.answers[answerIndex] = newNodeType;
         this.gameState = new GameState(this.parentNode.gameState, newNodeType);
         this.nodeType = newNodeType;
      }

      Enumeration childEnum = this.children();
      ArrayList orphanedChildren = new ArrayList();

      while(childEnum.hasMoreElements()) {
         PokerTreeNode child;
         if (!(child = (PokerTreeNode)childEnum.nextElement()).validateAndRefresh()) {
            orphanedChildren.add(child);
         }
      }

      Iterator orphanIter = orphanedChildren.iterator();

      while(orphanIter.hasNext()) {
         ((PokerTreeNode)orphanIter.next()).cleanupAndRemove();
      }

      if (this.gameState.gameStage < 4 && this.answers.length == 0) {
         this.expandDefaultChildren();
      }

      this.registerInCache();
      return true;
   }

   private boolean hasDuplicateAction(int candidateAction) {
      int[] answersRef;
      int answerCount = (answersRef = this.answers).length;

      for(int i = 0; i < answerCount; ++i) {
         int existingAction;
         if ((existingAction = answersRef[i]) != candidateAction && this.gameState.betSizesEqual(existingAction, candidateAction)) {
            return true;
         }
      }

      return false;
   }

   public final PokerTreeNode getChildByNodeType(int targetType) {
      Enumeration childEnum = this.children();

      while(childEnum.hasMoreElements()) {
         PokerTreeNode child;
         if ((child = (PokerTreeNode)childEnum.nextElement()).nodeType == targetType) {
            return child;
         }
      }

      return null;
   }

   public final boolean validateAndRefresh() {
      if (this.parentNode != null && this.parentNode.gameState != null) {
         if (this.parentNode.hasDuplicateAction(this.nodeType)) {
            return false;
         }

         if (this.parentNode.gameState.gameStage >= 4 || !this.parentNode.gameState.isActionAllowed(this.nodeType)) {
            return false;
         }

         this.gameState = new GameState(this.parentNode.gameState, this.nodeType);
      }

      Enumeration childEnum = this.children();
      ArrayList orphanedChildren = new ArrayList();

      while(childEnum.hasMoreElements()) {
         PokerTreeNode child;
         if (!(child = (PokerTreeNode)childEnum.nextElement()).validateAndRefresh()) {
            orphanedChildren.add(child);
         }
      }

      Iterator orphanIter = orphanedChildren.iterator();

      while(orphanIter.hasNext()) {
         ((PokerTreeNode)orphanIter.next()).cleanupAndRemove();
      }

      if (orphanedChildren.size() > 0 || this.gameState.gameStage < 4 && this.answers.length == 0) {
         this.expandDefaultChildren();
      }

      this.registerInCache();
      return true;
   }

   public final void registerInCache() {
      synchronized(nodeCache) {
         nodeCache.put(this.gameState, this);
      }
   }

   public final String toString() {
      if (this.nodeType < 0) {
         return this.descendantCount >= 0 ? this.gameState.nWay + "-WAY, " + STREET_NAMES[this.gameState.gameStage] + ", " + (AnalysisPanel.i == 0 ? "NO LIMIT" : "POT LIMIT " + this.descendantCount) : this.gameState.nWay + "-WAY, " + STREET_NAMES[this.gameState.gameStage] + ", " + (AnalysisPanel.i == 0 ? "NO LIMIT" : "POT LIMIT");
      } else {
         return this.descendantCount >= 0 ? solver.BetType.getBetCaption(this.parentNode.gameState, this.nodeType) + " (" + (int)(this.gameState.getTotalPot() / 1000.0D) + ") " + this.descendantCount : (solver.BetType.getBetCaption(this.parentNode.gameState, this.nodeType)) + " (" + (int)(this.gameState.getTotalPot() / 1000.0D) + ")";
      }
   }

   private int getThresholdValue(int[] thresholds, int facingBet, int limitMode) {
      return thresholds[((facingBet << 1) + limitMode << 2) + this.gameState.gameStage];
   }

   public final void expandDefaultChildren() {
      if (this.gameState.gameStage < 4) {

         int facingBet = this.gameState.isBefore(this.gameState.getCurrentAggressor()) ? 0 : 1;

         boolean allInCreatedForPotLimit = true;
         if (AnalysisPanel.i != 0) {
            allInCreatedForPotLimit = this.createChildNode(3, true) != null;
         }

         if (!allInCreatedForPotLimit || this.shouldSkipBet(facingBet)) {
            this.createChildNode(1, false);
         }

         if (!allInCreatedForPotLimit || this.shouldSkipFold(facingBet)) {
            this.createChildNode(0, true);
         }

         if (AnalysisPanel.i == 0 && this.shouldAddAllIn(facingBet)) {
            this.createChildNode(3, true);
         }

      }
   }

   private boolean shouldSkipBet(int facingBet) {
      return !this.gameState.isBetSizeValid((double)this.getThresholdValue(callCommittalThresholds, facingBet, AnalysisPanel.i) / 100.0D, 1);
   }

   private boolean shouldSkipFold(int facingBet) {
      return !this.gameState.isBetSizeValid((double)this.getThresholdValue(foldCommittalThresholds, facingBet, AnalysisPanel.i) / 100.0D, 0);
   }

   private boolean shouldAddAllIn(int facingBet) {
      facingBet = allInThresholds[(facingBet << 3) + this.gameState.gameStage];
      GameState allInState = new GameState(this.gameState, facingBet + 40000);
      return (new GameState(this.gameState, 3)).bets[this.gameState.firstPlayerToAct] <= allInState.bets[this.gameState.firstPlayerToAct];
   }

   public final PokerTreeNode createChildNode(int betType, boolean skipFilter) {
      while(true) {
         int[] answersRef = this.answers;
         int answerCount = answersRef.length;

         for(int i = 0; i < answerCount; ++i) {
            int existingAction = answersRef[i];
            if (this.gameState.betSizesEqual(betType, existingAction)) {
               return null;
            }
         }

         if (!this.gameState.isActionAllowed(betType)) {
            if (betType != 6) {
               return null;
            }

            skipFilter = true;
            betType = 1;
         } else {
            if (!skipFilter && activeFilterSettings != null && activeFilterSettings.matches(this.gameState, betType)) {
               if (betType == 6) {
                  skipFilter = true;
                  betType = 1;
                  continue;
               }

               return null;
            }

            PokerTreeNode newChild = new PokerTreeNode(this, betType, true);
            this.addToAnswers(newChild.nodeType);
            ((DefaultTreeModel)AnalysisPanel.INSTANCE.gameTree.getModel()).insertNodeInto(newChild, this, 0);

            newChild.registerInCache();
            return newChild;
         }
      }
   }

   private final void addToAnswers(int action) {
      int[] newAnswers = new int[this.answers.length + 1];
      System.arraycopy(this.answers, 0, newAnswers, 0, this.answers.length);
      if (action > 80000) {
         action = (this.gameState.bets[this.gameState.lastAggressorIndex] - this.gameState.getMaxBet()) / 1000;
      }
      newAnswers[this.answers.length] = action;
      this.answers = newAnswers;
   }

   public final void cleanupAndRemove() {
      PokerTreeNode self = this;
      synchronized(nodeCache) {
         nodeCache.remove(self.gameState);
      }

      int[] filteredAnswers = new int[this.parentNode.answers.length - 1];
      int writeIdx = 0;

      for(int readIdx = 0; readIdx < this.parentNode.answers.length; ++readIdx) {
         if (this.parentNode.answers[readIdx] != this.nodeType) {
            filteredAnswers[writeIdx] = this.parentNode.answers[readIdx];
            ++writeIdx;
         }
      }

      this.parentNode.answers = filteredAnswers;
      ((DefaultTreeModel)AnalysisPanel.INSTANCE.gameTree.getModel()).removeNodeFromParent(this);
   }

   public final void removeMatchingNodes(List matchTypes) {
      ArrayList matchedChildren = new ArrayList();
      Enumeration childEnum = this.children();

      while(childEnum.hasMoreElements()) {
         PokerTreeNode child = (PokerTreeNode)childEnum.nextElement();
         Iterator typeIter = matchTypes.iterator();

         while(typeIter.hasNext()) {
            BetType type = (BetType)typeIter.next();
            if (child.nodeType == type.intValue()) {
               matchedChildren.add(child);
            }
         }
      }

      Iterator matchIter = matchedChildren.iterator();

      while(matchIter.hasNext()) {
         ((PokerTreeNode)matchIter.next()).cleanupAndRemove();
      }

      childEnum = this.children();

      while(childEnum.hasMoreElements()) {
         ((PokerTreeNode)childEnum.nextElement()).removeMatchingNodes(matchTypes);
      }

   }

   public final void applyFilterSettings(FilterSettings rootSettings, ArrayList parentNodes) {
      if (this.gameState.gameStage < 4) {
         int facingBet = this.gameState.isBefore(this.gameState.getCurrentAggressor()) ? 0 : 1;
         double betThreshold = (double)this.getThresholdValue(betCommittalThresholds, facingBet, AnalysisPanel.i) / 100.0D;
         FilterSettings currentSettings = rootSettings;

         gnu.wrapper.set.IntSet addedTypes;
         label97:
         for(addedTypes = parentNodes == null ? null : new gnu.wrapper.set.IntSet(5); currentSettings != null; currentSettings = currentSettings.l) {
            activeFilterSettings = currentSettings;
            int levelIdx = this.gameState.raiseCount;
            if (currentSettings.o == null && currentSettings.p == null) {
               String[] segmentTexts;
               ArrayList[] parsedLevels = new ArrayList[(segmentTexts = currentSettings.q.textField.getText().trim().split(";")).length];

               for(int i = 0; i < parsedLevels.length; ++i) {
                  parsedLevels[i] = BetSizingPanel.parseBetSizes(segmentTexts[i]);
               }

               currentSettings.o = parsedLevels;
               String fullText;
               int lastSeparator = (fullText = currentSettings.q.textField.getText()).lastIndexOf(59) + 1;
               currentSettings.p = (List)((fullText = fullText.substring(lastSeparator).trim()).length() > 0 ? BetSizingPanel.parseBetSizes(fullText) : new ArrayList());
            }

            Iterator typeIter = (currentSettings.o != null && levelIdx < currentSettings.o.length ? currentSettings.o[levelIdx] : currentSettings.p).iterator();

            while(true) {
               BetType candidateType;
               do {
                  if (!typeIter.hasNext()) {
                     continue label97;
                  }

                  if ((candidateType = (BetType)typeIter.next()).intValue() == -1) {
                     candidateType = solver.BetType.suggest(this.gameState);
                  }
               } while(candidateType.intValue() != 3 && this.gameState.isBetSizeValid(betThreshold, candidateType));

               if (this.createChildNode(candidateType.intValue(), false) != null && addedTypes != null) {
                  addedTypes.add(candidateType.intValue());
               }
            }
         }

         activeFilterSettings = null;
         Enumeration childEnum = this.children();

         while(true) {
            while(childEnum.hasMoreElements()) {
               PokerTreeNode child = (PokerTreeNode)childEnum.nextElement();
               if (parentNodes != null && addedTypes.contains(child.nodeType)) {
                  child.applyFilterSettings((FilterSettings)rootSettings, (ArrayList)null);
                  parentNodes.add(child);
               } else {
                  child.applyFilterSettings(rootSettings, parentNodes);
               }
            }

            return;
         }
      }
   }

   public final int countNodesAtStreet(int targetStage, int targetFirstPlayer, int extraCount) {
      if (this.gameState.gameStage > targetStage) {
         return 0;
      } else {
         int total = 0;

         PokerTreeNode child;
         for(Enumeration childEnum = this.children(); childEnum.hasMoreElements(); total += child.countNodesAtStreet(targetStage, targetFirstPlayer, extraCount)) {
            child = (PokerTreeNode)childEnum.nextElement();
         }

         return this.gameState.gameStage == targetStage && this.gameState.firstPlayerToAct == targetFirstPlayer ? total + this.children.size() + extraCount : total;
      }
   }

   public final long countFlopNodes(int extraCount) {
      if (this.gameState.gameStage > 1) {
         return 0L;
      } else {
         long total = 0L;

         PokerTreeNode child;
         for(Enumeration childEnum = this.children(); childEnum.hasMoreElements(); total += child.countFlopNodes(extraCount)) {
            child = (PokerTreeNode)childEnum.nextElement();
         }

         return this.gameState.gameStage == 1 ? total + (long)this.children.size() + (long)extraCount : total;
      }
   }

   public final long countTurnNodes(int extraCount) {
      if (this.gameState.gameStage > 2) {
         return 0L;
      } else {
         long total = 0L;

         PokerTreeNode child;
         for(Enumeration childEnum = this.children(); childEnum.hasMoreElements(); total += child.countTurnNodes(extraCount)) {
            child = (PokerTreeNode)childEnum.nextElement();
         }

         return this.gameState.gameStage == 2 ? total + (long)this.answers.length + (long)extraCount : total;
      }
   }

   public final long countTerminalNodes() {
      if (this.gameState.gameStage >= 4) {
         return 1L;
      } else {
         long total = 0L;

         PokerTreeNode child;
         for(Enumeration childEnum = this.children(); childEnum.hasMoreElements(); total += child.countTerminalNodes()) {
            child = (PokerTreeNode)childEnum.nextElement();
         }

         return total;
      }
   }

   public final long countRiverNodes(int extraCount) {
      if (this.gameState.gameStage > 3) {
         return 0L;
      } else {
         long total = 0L;

         PokerTreeNode child;
         for(Enumeration childEnum = this.children(); childEnum.hasMoreElements(); total += child.countRiverNodes(extraCount)) {
            child = (PokerTreeNode)childEnum.nextElement();
         }

         return this.gameState.gameStage == 3 ? total + (long)this.children.size() + (long)extraCount : total;
      }
   }

   public final int calculateDescendantCount() {
      if (this.gameState.gameStage >= 4) {
         return 0;
      } else {
         int count = 0;
         if (this.children != null) {
            for(Enumeration childEnum = this.children(); childEnum.hasMoreElements(); ++count) {
               PokerTreeNode child = (PokerTreeNode)childEnum.nextElement();
               count += child.calculateDescendantCount();
            }
         }

         this.descendantCount = count;
         return count;
      }
   }
}
