package solver;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public final class ScriptConfig {
   String[] boards;
   List stacksList;
   File saveFolder;
   SaveOptionsPanel saveOptions;
   int boardIndex = 0;
   int stacksIndex = 1;
   int runToIterations = 30;
   int resetAvgIterations = 15;
   int unusedSentinel = -1;
   boolean evResetTriggered = false;
   File treeFile;
   private String filenamePrefix;

   public ScriptConfig(String filename, File treeFile, SaveOptionsPanel saveOptions, String saveFolderPath, String[] boards, List stacksList, int runToIterations, int resetAvgIterations) throws Throwable {
      if (SaveDirectoryListener.getGameStage(treeFile) > 0) {
         this.boards = boards;
      } else {
         this.boards = new String[]{solver.HashUtil.decodeBy1(new char[0])};
      }

      this.runToIterations = runToIterations;
      this.resetAvgIterations = resetAvgIterations;
      this.saveOptions = saveOptions;
      this.stacksList = stacksList;
      this.treeFile = treeFile;
      this.filenamePrefix = filename;
      this.saveFolder = new File(saveFolderPath);
      if (!this.saveFolder.exists()) {
         this.saveFolder = new File(FlopNE.getSavedRunsDirectory(), "script");

         for(int n = 2; this.saveFolder.exists(); ++n) {
            this.saveFolder = new File(FlopNE.getSavedRunsDirectory(), "script" + n);
         }
      }

      if (this.boards.length == 1) {
         this.stacksIndex = 0;
      }

   }

   public final boolean hasMoreScenarios() {
      return this.currentScenarioIndex() < this.totalScenarioCount();
   }

   public static final String formatStacks(int[] stacks) {
      if (stacks == null) {
         return solver.HashUtil.decodeBy4(new char[0]);
      } else {
         for(int i = 1; i < stacks.length; ++i) {
            if (stacks[i] != stacks[i - 1]) {
               return Arrays.toString(stacks);
            }
         }

         return solver.HashUtil.decodeBy5(new char[0]) + stacks[0];
      }
   }

   public final int totalScenarioCount() {
      return this.boards.length * this.boardVariantCount();
   }

   public final int currentScenarioIndex() {
      return this.boards.length == 1 ? this.stacksIndex : (this.stacksIndex - 1) * this.boards.length + this.boardIndex;
   }

   public final String currentBoard() {
      return this.boards[this.boardIndex - 1];
   }

   public final int[] currentStacks() {
      return this.stacksList == null ? null : (int[])this.stacksList.get(this.stacksIndex - 1);
   }

   private int boardVariantCount() {
      return this.stacksList == null ? 1 : this.stacksList.size();
   }

   public final void saveSimulation() {
      Thread saveThread = FlopNE.saveSimulation(new File(this.saveFolder, this.filenamePrefix + "" + this.currentBoard() + formatStacks(this.currentStacks()) + ".mkr"), this.saveOptions.getCompressionMode());

      try {
         saveThread.join();
      } catch (InterruptedException ex) {
         ex.printStackTrace();
      }
   }
}
