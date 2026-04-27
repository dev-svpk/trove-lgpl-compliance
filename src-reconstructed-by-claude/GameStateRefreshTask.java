package solver;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

final class GameStateRefreshTask implements Runnable {
   private JTable table;
   boolean cancelled = false;
   private static GameStateRefreshTask currentTask;
   private static GameState lastGameState;

   public GameStateRefreshTask(JTable table) {
      this.table = table;
      if (currentTask != null) {
         currentTask.cancelled = true;
      }

      currentTask = this;
   }

   public final void run() {
      if (FlopNE.gameState != null) {
         GameState currentState = MainTabbedPane.getCurrentGameState();
         if (lastGameState != null && !lastGameState.hasSameState(currentState)) {
            SwingUtilities.invokeLater(() -> {
               this.table.setModel(new DynamicTableModel());
            });
         }

         lastGameState = currentState;
         double maxBet = Arrays.stream(FlopNE.gameState.bets).max().getAsInt();
         if ((currentState.gameStage != 1 || MainTabbedPane.enteredBoard.size() >= 3) && (currentState.gameStage != 2 || MainTabbedPane.enteredBoard.size() >= 4) && (currentState.gameStage != 3 || MainTabbedPane.enteredBoard.size() >= 5)) {
            BucketGenerator bucketGen = FlopNE.buildBucketGeneratorHoldem(this, currentState, MainTabbedPane.enteredBoard);
            if (!this.cancelled) {
               int[] availableActions = FlopNE.getAvailableActions(currentState);
               ArrayList captions = new ArrayList();
               int colIdx = 0;
               if (bucketGen != null) {
                  int[] actions = availableActions;
                  int n = availableActions.length;

                  for(int i = 0; i < n; ++i) {
                     int action = actions[i];

                     captions.add(solver.HashUtil.decodeBy42(new char[0]) + solver.BetType.getBetCaption(currentState, action) + " (" + bucketGen.bucketLabels[colIdx] + ")");

                     ++colIdx;
                  }

                  for(colIdx = 0; colIdx < bucketGen.bucketStats.length; ++colIdx) {
                     if (bucketGen.bucketStats[colIdx].length == 0) {
                        bucketGen.bucketStats[colIdx] = new String[]{solver.HashUtil.decodeBy43(new char[0])};
                     }
                  }

                  String[] captionsArr = (String[])captions.toArray(new String[0]);
                  DynamicTableModel tableModel = new DynamicTableModel();
                  Integer[] sortedIds;
                  int sortedLen = (sortedIds = EquitySortComparator.sortActions(FlopNE.getAvailableActions(currentState))).length;

                  for(int j = 0; j < sortedLen; ++j) {
                     n = sortedIds[j];
                     colIdx = EquitySortComparator.findActionIndex(currentState, n);
                     tableModel.addColumn(captionsArr[colIdx], bucketGen.bucketStats[colIdx]);
                  }

                  SwingUtilities.invokeLater(() -> {
                     this.table.setModel(tableModel);
                  });
               }
            }
         }
      }

   }
}
