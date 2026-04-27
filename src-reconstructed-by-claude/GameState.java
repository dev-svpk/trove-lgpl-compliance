package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public final class GameState {
   static int defaultStackSize;
   int nWay;
   int[] stacks;
   int[] deadMoney;

   int nodeType;
   int[] bets;
   private int checkedBits;
   int foldedBits;
   int gameStage; //0 - preflop; 1 - flop; 2 - turn; 3 - river;
   private long stateHash;
   GameState parentNode;
   int firstPlayerToAct;
   int lastAggressorIndex;
   int raiseCount;
   int nodeId;
   private int priorMaxBet;
   boolean noAnte;
   private double totalPot = -1;
   private double totalPotWithSquid = -1;
   private GameState rootNodeCache;
   boolean splitPayment;

   static {
      String[] stages = new String[]{"PRE", "FLOP", "TURN", "RIVER"};
      defaultStackSize = 100000;
   }

   public GameState(int[] blinds, int playerCount, int stage, int deadMoney, int firstPlayerToAct, int newAnte, int whoStarts) {
      this.nWay = 2;
      this.gameStage = 0;
      this.stateHash = 274572457L;
      this.lastAggressorIndex = -1;
      this.deadMoney = new int[]{deadMoney};
      this.nWay = playerCount;
      this.bets = new int[playerCount];
      this.foldedBits = 0;
      this.checkedBits = 0;
      this.stacks = new int[playerCount];

      int blindIndex;

      if (defaultStackSize != -1) {
         fillArray(this.stacks, defaultStackSize);
      }

      if ( (whoStarts >= playerCount) || (whoStarts == -1) ) {
         whoStarts = 1;
      }

      System.lineSeparator();
      if (firstPlayerToAct < 0) {
         if (stage > 0) {
            this.firstPlayerToAct = 1;
         } else if (playerCount == 2) {
            this.bets[0] = blinds[0] * 100;
            this.bets[1] = blinds[1] * 100;
            if (this.bets[1] > this.bets[0]) {
               this.firstPlayerToAct = 0;
            } else {
               this.firstPlayerToAct = 1;
            }
         } else {
            for(blindIndex = 0; blindIndex < blinds.length; ++blindIndex) {
               this.bets[(blindIndex + 1) % playerCount] = blinds[blindIndex] * 100;
               if (blinds[blindIndex] != 0) {
                  this.firstPlayerToAct = (blindIndex + 2) % playerCount;
               }
            }
         }
      } else {
         this.firstPlayerToAct = firstPlayerToAct;
         if (playerCount == 2) {
            this.bets[0] = blinds[0] * 100;
            this.bets[1] = blinds[1] * 100;
         } else {

            for(blindIndex = 0; blindIndex < blinds.length; ++blindIndex) {
               this.bets[(blindIndex + whoStarts) % playerCount] = blinds[blindIndex] * 100;
            }
         }
      }

      for (int i = 0; i < this.bets.length; i++) {
         this.bets[i] += newAnte * 100;
      }

      this.nodeType = -1;
      this.gameStage = stage;
   }

   public GameState(int playerCount, int gameStage, int deadMoneyAmount, int newAnte, int whoStarts) {
      this.nWay = 2;
      this.gameStage = 0;
      this.stateHash = 274572457L;
      this.lastAggressorIndex = -1;
      this.deadMoney = new int[]{deadMoneyAmount};
      this.nWay = playerCount;
      this.bets = new int[playerCount];
      this.foldedBits = 0;
      this.checkedBits = 0;
      this.stacks = new int[playerCount];
      this.splitPayment = splitPayment;

      if (defaultStackSize != -1) {
         fillArray(this.stacks, defaultStackSize);
      }

      if (whoStarts >= playerCount) {
         whoStarts = -1;
      }

      if (gameStage == 0) {
         if (playerCount > 2) {
            this.bets[2] = 2000;
            this.bets[1] = 1000;
         } else {
            this.bets[1] = 2000;
            this.bets[0] = 1000;
         }
      }

      this.firstPlayerToAct = playerCount == 2 ? 0 : 3 % playerCount;
      if (gameStage > 0) {
         this.firstPlayerToAct = 1;
      }

      for (int i = 0; i < this.bets.length; i++) {
         this.bets[i] += newAnte * 100;
      }

      this.nodeType = -1;
      this.gameStage = gameStage;
   }

   public GameState(GameState other) {
      this.nWay = 2;
      this.gameStage = 0;
      this.stateHash = 274572457L;
      this.lastAggressorIndex = -1;
      this.stacks = other.stacks;
      this.priorMaxBet = other.priorMaxBet;
      this.deadMoney = other.deadMoney;
      this.nodeType = other.nodeType;
      this.gameStage = other.gameStage;
      this.splitPayment = other.splitPayment;

      if (other.parentNode != null) {
         this.parentNode = new GameState(other.parentNode);
      }

      this.stateHash = other.stateHash;
      this.bets = copyIntArray(other.bets);
      this.raiseCount = other.raiseCount;
      this.nWay = other.nWay;
      this.firstPlayerToAct = other.firstPlayerToAct;
      this.foldedBits = other.foldedBits;
      this.checkedBits = other.checkedBits;
      this.lastAggressorIndex = other.lastAggressorIndex;
   }

   public final boolean checkedAroundPreviously() {
      // Must have a parent node
      if (this.parentNode == null) {
         return false;
      }

      // Must be at the start of a new game stage
      if (this.gameStage <= this.parentNode.gameStage) {
         return false;
      }

      // Start from the parent node (last action of previous street)
      GameState node = this.parentNode;
      int prevGameStage = node.gameStage;

      // Traverse back through all actions in the previous game stage
      while (node != null && node.gameStage == prevGameStage) {
         // If we find any action that's not a check/call (nodeType 1) or fold (nodeType 0)
         // and not the initial node (nodeType -1), then it wasn't checked around
         if (node.nodeType != 1 && node.nodeType != 0 && node.nodeType != -1) {
            return false;
         }
         node = node.parentNode;
      }

      return true;
   }

   public GameState(GameState parentState, BetType betType) {
      this(parentState, betType.intValue());
   }

   public GameState(GameState parentState, int actionType) {
      this.nWay = 2;
      this.gameStage = 0;
      this.stateHash = 274572457L;
      this.lastAggressorIndex = -1;
      this.deadMoney = parentState.deadMoney;
      this.splitPayment = parentState.splitPayment;
      this.priorMaxBet = parentState.priorMaxBet;
      this.stacks = parentState.stacks;
      this.nWay = parentState.nWay;
      this.parentNode = parentState;
      this.stateHash = parentState.stateHash;
      this.lastAggressorIndex = parentState.firstPlayerToAct;
      this.gameStage = parentState.gameStage;
      this.raiseCount = parentState.raiseCount;
      this.firstPlayerToAct = parentState.firstPlayerToAct;
      this.bets = copyIntArray(parentState.bets);
      this.foldedBits = parentState.foldedBits;
      this.checkedBits = parentState.checkedBits;
      this.lastAggressorIndex = this.firstPlayerToAct;
      int currentPlayer = this.firstPlayerToAct;
      this.checkedBits = solver.Equity.setBit(this.checkedBits, currentPlayer);
      this.nodeType = actionType;
      this.stateHash = this.stateHash * 27001L + 1L + (long)actionType ^ 120007L * (long)(actionType + 1);
      int[] betArray;
      int raiseAmount;
      switch(actionType) {
         case 0:
            this.foldedBits = solver.Equity.setBit(this.foldedBits, currentPlayer);
            if (!this.isHandOver() && !this.isTerminalAllIn()) {
               if (this.isStreetComplete()) {
                  this.advanceToNewStreetFirstPlayer();
                  ++this.gameStage;
                  this.checkedBits = 0;
                  this.raiseCount = 0;
               } else {
                  this.advanceToNextActivePlayer();
               }
            } else {
               this.gameStage = 4;
            }
            break;
         case 1:
            this.bets[currentPlayer] = Math.min(this.stacks[currentPlayer], this.getMaxBet());
            if (this.isTerminalAllIn()) {
               this.gameStage = 4;
            } else if (this.isStreetComplete()) {
               this.advanceToNewStreetFirstPlayer();
               ++this.gameStage;
               this.checkedBits = 0;
               this.raiseCount = 0;
            } else {
               this.advanceToNextActivePlayer();
            }
            break;
         case 2:
            this.bets[currentPlayer] = this.getMaxBet();
            betArray = this.bets;
            betArray[currentPlayer] = (int)((double)betArray[currentPlayer] + this.getTotalPot());
            this.advanceToNextActivePlayer();
            break;
         case 3:
            this.bets[currentPlayer] = this.stacks[currentPlayer];
            if (this.isTerminalAllIn()) {
               this.gameStage = 4;
            } else {
               this.advanceToNextActivePlayer();
            }
            break;
         case 4:
            this.bets[currentPlayer] = this.getMaxBet();
            betArray = this.bets;
            betArray[currentPlayer] = (int)((double)betArray[currentPlayer] + this.getTotalPot() / 2.0D);
            this.advanceToNextActivePlayer();
            break;
         case 5:
            if (this.nodeType != 1 && this.nodeType != -1) {
               raiseAmount = this.getMaxBet() - this.bets[currentPlayer];
               if (this.gameStage == 0) {
                  raiseAmount = Math.max(raiseAmount, this.getBigBlind());
               }

               this.bets[currentPlayer] = this.getMaxBet();
               betArray = this.bets;
               betArray[currentPlayer] += raiseAmount;
            } else {
               this.bets[currentPlayer] = this.getMaxBet();
               betArray = this.bets;
               betArray[currentPlayer] += this.getBigBlind();
            }

            this.advanceToNextActivePlayer();
            break;
         case 6:
            this.bets[currentPlayer] = this.getMaxBet();
            if (this.gameStage <= 1) {
               betArray = this.bets;
               betArray[currentPlayer] += this.getBigBlind();
            } else {
               betArray = this.bets;
               betArray[currentPlayer] += this.getBigBlind() << 1;
            }

            this.advanceToNextActivePlayer();
            break;
         case 7:
            this.bets[currentPlayer] = this.getMaxBet();
            betArray = this.bets;
            betArray[currentPlayer] = (int)((double)betArray[currentPlayer] + this.getTotalPot() / 4.0D);
            this.advanceToNextActivePlayer();
            break;
         case 8:
            this.bets[currentPlayer] = this.getMaxBet();
            betArray = this.bets;
            betArray[currentPlayer] = (int)((double)betArray[currentPlayer] + 2.0D * this.getTotalPot());
            this.advanceToNextActivePlayer();
            break;
         case 9:
            this.bets[currentPlayer] = this.getMaxBet();
            betArray = this.bets;
            betArray[currentPlayer] = (int)((double)betArray[currentPlayer] + 3.0D * this.getTotalPot() / 4.0D);
            this.advanceToNextActivePlayer();
            break;
         case 10:
            if (this.nodeType != 1 && this.nodeType != -1) {
               raiseAmount = this.getMaxBet() - this.bets[currentPlayer];
               if (this.gameStage == 0) {
                  raiseAmount = Math.max(raiseAmount, this.getBigBlind());
               }

               this.bets[currentPlayer] = this.getMaxBet();
               betArray = this.bets;
               betArray[currentPlayer] += raiseAmount;
            } else {
               this.bets[currentPlayer] = this.getMaxBet();
               betArray = this.bets;
               betArray[currentPlayer] += this.getBigBlind();
            }

            this.advanceToNextActivePlayer();
            break;
         default:
            if (actionType > 80000) {
               raiseAmount = actionType - 80000;
               this.bets[currentPlayer] = this.getMaxBet();
               betArray = this.bets;
               betArray[currentPlayer] = (int)((double)betArray[currentPlayer] + this.getTotalPotWithSquid() * (double)raiseAmount / 100.0D);
               this.advanceToNextActivePlayer();
            } else if (actionType > 40000) {
               raiseAmount = actionType - 40000;
               this.bets[currentPlayer] = this.getMaxBet();
               betArray = this.bets;
               betArray[currentPlayer] = (int)((double)betArray[currentPlayer] + this.getTotalPot() * (double)raiseAmount / 100.0D);
               this.advanceToNextActivePlayer();
            } else {
               raiseAmount = actionType - solver.BetType.CENTS_OFFSET;
               this.bets[currentPlayer] = this.getMaxBet();
               betArray = this.bets;
               betArray[currentPlayer] += raiseAmount * 2000 / 2;
               this.advanceToNextActivePlayer();
            }
      }

      raiseAmount = this.getMaxOpponentStack(currentPlayer);
      if (this.bets[currentPlayer] > raiseAmount) {
         this.bets[currentPlayer] = raiseAmount;
      }

      if (this.bets[currentPlayer] > this.stacks[currentPlayer]) {
         this.bets[currentPlayer] = this.stacks[currentPlayer];
      }

      if (actionType != 0 && actionType != 1 && actionType != 3) {
         ++this.raiseCount;
      }

   }

   public GameState(GameState parentState, double betType) {
      int actionType = (int)betType;

      this.nWay = 2;
      this.gameStage = 0;
      this.stateHash = 274572457L;
      this.lastAggressorIndex = -1;
      this.deadMoney = parentState.deadMoney;
      this.splitPayment = parentState.splitPayment;
      this.priorMaxBet = parentState.priorMaxBet;
      this.stacks = parentState.stacks;
      this.nWay = parentState.nWay;
      this.parentNode = parentState;
      this.stateHash = parentState.stateHash;
      this.lastAggressorIndex = parentState.firstPlayerToAct;
      this.gameStage = parentState.gameStage;
      this.raiseCount = parentState.raiseCount;
      this.firstPlayerToAct = parentState.firstPlayerToAct;
      this.bets = copyIntArray(parentState.bets);
      this.foldedBits = parentState.foldedBits;
      this.checkedBits = parentState.checkedBits;
      this.lastAggressorIndex = this.firstPlayerToAct;
      int currentPlayer = this.firstPlayerToAct;
      this.checkedBits = solver.Equity.setBit(this.checkedBits, currentPlayer);
      this.nodeType = actionType;
      this.stateHash = this.stateHash * 27001L + 1L + (long)actionType ^ 120007L * (long)(actionType + 1);
      int[] betArray;
      int raiseAmount;
      switch(actionType) {
         case 0:
            this.foldedBits = solver.Equity.setBit(this.foldedBits, currentPlayer);
            if (!this.isHandOver() && !this.isTerminalAllIn()) {
               if (this.isStreetComplete()) {
                  this.advanceToNewStreetFirstPlayer();
                  ++this.gameStage;
                  this.checkedBits = 0;
                  this.raiseCount = 0;
               } else {
                  this.advanceToNextActivePlayer();
               }
            } else {
               this.gameStage = 4;
            }
            break;
         case 1:
            this.bets[currentPlayer] = Math.min(this.stacks[currentPlayer], this.getMaxBet());
            if (this.isTerminalAllIn()) {
               this.gameStage = 4;
            } else if (this.isStreetComplete()) {
               this.advanceToNewStreetFirstPlayer();
               ++this.gameStage;
               this.checkedBits = 0;
               this.raiseCount = 0;
            } else {
               this.advanceToNextActivePlayer();
            }
            break;
         case 2:
            this.bets[currentPlayer] = this.getMaxBet();
            betArray = this.bets;
            betArray[currentPlayer] = (int)((double)betArray[currentPlayer] + this.getTotalPot());
            this.advanceToNextActivePlayer();
            break;
         case 3:
            this.bets[currentPlayer] = this.stacks[currentPlayer];
            if (this.isTerminalAllIn()) {
               this.gameStage = 4;
            } else {
               this.advanceToNextActivePlayer();
            }
            break;
         case 4:
            this.bets[currentPlayer] = this.getMaxBet();
            betArray = this.bets;
            betArray[currentPlayer] = (int)((double)betArray[currentPlayer] + this.getTotalPot() / 2.0D);
            this.advanceToNextActivePlayer();
            break;
         case 5:
            if (this.nodeType != 1 && this.nodeType != -1) {
               raiseAmount = this.getMaxBet() - this.bets[currentPlayer];
               if (this.gameStage == 0) {
                  raiseAmount = Math.max(raiseAmount, this.getBigBlind());
               }

               this.bets[currentPlayer] = this.getMaxBet();
               betArray = this.bets;
               betArray[currentPlayer] += raiseAmount;
            } else {
               this.bets[currentPlayer] = this.getMaxBet();
               betArray = this.bets;
               betArray[currentPlayer] += this.getBigBlind();
            }

            this.advanceToNextActivePlayer();
            break;
         case 6:
            this.bets[currentPlayer] = this.getMaxBet();
            if (this.gameStage <= 1) {
               betArray = this.bets;
               betArray[currentPlayer] += this.getBigBlind();
            } else {
               betArray = this.bets;
               betArray[currentPlayer] += this.getBigBlind() << 1;
            }

            this.advanceToNextActivePlayer();
            break;
         case 7:
            this.bets[currentPlayer] = this.getMaxBet();
            betArray = this.bets;
            betArray[currentPlayer] = (int)((double)betArray[currentPlayer] + this.getTotalPot() / 4.0D);
            this.advanceToNextActivePlayer();
            break;
         case 8:
            this.bets[currentPlayer] = this.getMaxBet();
            betArray = this.bets;
            betArray[currentPlayer] = (int)((double)betArray[currentPlayer] + 2.0D * this.getTotalPot());
            this.advanceToNextActivePlayer();
            break;
         case 9:
            this.bets[currentPlayer] = this.getMaxBet();
            betArray = this.bets;
            betArray[currentPlayer] = (int)((double)betArray[currentPlayer] + 3.0D * this.getTotalPot() / 4.0D);
            this.advanceToNextActivePlayer();
            break;
         case 10:
            if (this.nodeType != 1 && this.nodeType != -1) {
               raiseAmount = this.getMaxBet() - this.bets[currentPlayer];
               if (this.gameStage == 0) {
                  raiseAmount = Math.max(raiseAmount, this.getBigBlind());
               }

               this.bets[currentPlayer] = this.getMaxBet();
               betArray = this.bets;
               betArray[currentPlayer] += raiseAmount;
            } else {
               this.bets[currentPlayer] = this.getMaxBet();
               betArray = this.bets;
               betArray[currentPlayer] += this.getBigBlind();
            }

            this.advanceToNextActivePlayer();
            break;
         default:
            if (actionType > 40000) {
               raiseAmount = actionType - 40000;
               this.bets[currentPlayer] = this.getMaxBet();
               betArray = this.bets;
               betArray[currentPlayer] = (int)((double)betArray[currentPlayer] + this.getTotalPot() * (double)raiseAmount / 100.0D);
               this.advanceToNextActivePlayer();
            } else {
               double raiseAmountDouble = betType - solver.BetType.CENTS_OFFSET;
               //var4 = var2 - solver.BetType.CENTS_OFFSET;
               this.bets[currentPlayer] = this.getMaxBet();
               betArray = this.bets;
               betArray[currentPlayer] += (int)(raiseAmountDouble * 2000 / 2);
               this.advanceToNextActivePlayer();
            }
      }

      raiseAmount = this.getMaxOpponentStack(currentPlayer);
      if (this.bets[currentPlayer] > raiseAmount) {
         this.bets[currentPlayer] = raiseAmount;
      }

      if (this.bets[currentPlayer] > this.stacks[currentPlayer]) {
         this.bets[currentPlayer] = this.stacks[currentPlayer];
      }

      if (actionType != 0 && actionType != 1 && actionType != 3) {
         ++this.raiseCount;
      }

   }

   public final double getTotalPot() {
      if (totalPot < 0) {
         totalPot = Arrays.stream(this.bets).sum() + this.deadMoney[0];
      }
      return totalPot;
   }

   public final double getTotalPotWithSquid() {
      if (totalPotWithSquid < 0) {
         totalPotWithSquid = Arrays.stream(this.bets).sum() + FlopNE.squidPrice * this.getMaxBet();
      }
      return totalPotWithSquid;
   }

   private double getPotMinusUncalledBet() {
      int maxBet = -1;
      int secondMaxBet = -1;
      int[] betsArray;
      int betsLength = (betsArray = this.bets).length;

      for(int i = 0; i < betsLength; ++i) {
         int bet;
         if ((bet = betsArray[i]) > maxBet) {
            secondMaxBet = maxBet;
            maxBet = bet;
         } else if (bet > secondMaxBet) {
            secondMaxBet = bet;
         }
      }

      if (secondMaxBet < 0) {
         System.exit(0);
      }

      return this.getTotalPot() - (double)(maxBet - secondMaxBet);
   }

   public final double computeRake() {
      if (this.parentNode.gameStage == 0 && this.isHandOver() && !solver.Equity.isBitSet(GameSettings.rakeFlags, 0)) {
         return 0.0D;
      } else if (solver.Equity.isBitSet(GameSettings.rakeFlags, 1)) {
         return this.getTotalPot();
      } else if (solver.Equity.isBitSet(GameSettings.rakeFlags, 2)) {
         return this.getPotMinusUncalledBet();
      } else {
         double callerBet = (double)this.findCallerNode().getMaxBet();
         double rakeAmount = 0.0D;
         int[] betsArray;
         int betsLength = (betsArray = this.bets).length;

         for(int i = 0; i < betsLength; ++i) {
            int playerBet;
            if ((double)(playerBet = betsArray[i]) > callerBet) {
               rakeAmount += callerBet;
            } else {
               rakeAmount += (double)playerBet;
            }
         }

         return rakeAmount + (double)this.deadMoney[0];
      }
   }

   public final boolean isBefore(int playerIndex) {
      if (playerIndex == 0) {
         return false;
      } else {
         return this.firstPlayerToAct == 0 || this.firstPlayerToAct > playerIndex;
      }
   }

   public final int getCurrentAggressor() {
      for(GameState node = this; node != null; node = node.parentNode) {
         if (!node.isFold() && !node.isCall()) {
            return node.lastAggressorIndex;
         }
      }

      if (this.nWay == 2) {
         return 0;
      } else {
         return 3 % this.nWay;
      }
   }

   public final int getBetIncrement(int playerIndex) {
      return this.bets[playerIndex] - this.priorMaxBet;
   }

   public final int getCalledBetTotal() {
      int total = 0;

      for(int playerIndex = 0; playerIndex < this.nWay; ++playerIndex) {
         total += Math.min(this.priorMaxBet, this.bets[playerIndex]);
      }

      return total + this.deadMoney[0];
   }

   private static void fillArray(int[] array, int value) {
      for(int i = 0; i < array.length; ++i) {
         array[i] = value;
      }

   }

   private void advanceToNextActivePlayer() {
      if (this.isTerminalAllIn()) {
         this.gameStage = 4;
      } else {
         this.firstPlayerToAct = (this.firstPlayerToAct + 1) % this.nWay;

         while(solver.Equity.isBitSet(this.foldedBits, this.firstPlayerToAct) || this.bets[this.firstPlayerToAct] >= this.stacks[this.firstPlayerToAct]) {
            this.advanceToNextActivePlayer();
         }

      }
   }

   private void advanceToNewStreetFirstPlayer() {
      for(this.firstPlayerToAct = 1; solver.Equity.isBitSet(this.foldedBits, this.firstPlayerToAct) || this.bets[this.firstPlayerToAct] >= this.stacks[this.firstPlayerToAct]; this.firstPlayerToAct = (this.firstPlayerToAct + 1) % this.nWay) {
      }

      this.priorMaxBet = this.getMaxBet();
   }

   public final int getMaxBet() {
      int maxBet = 0;

      for(int i = 0; i < this.bets.length; ++i) {
         if (this.bets[i] > maxBet) {
            maxBet = this.bets[i];
         }
      }

      return maxBet;
   }

   public final GameState findCallerNode() {
      GameState node = this;


      while(node.nodeType != 1) {
         if (node.parentNode == null) {
            return node;
         }

         node = node.parentNode;
      }

      return node;
   }

   public final boolean isFold() {
      return this.nodeType == 0;
   }

   public final boolean isCall() {
      return this.nodeType == 1;
   }

   public final int getMaxOpponentStack(int playerIndex) {
      int maxStack = 0;

      for(int i = 0; i < this.nWay; ++i) {
         if (i != playerIndex && !solver.Equity.isBitSet(this.foldedBits, i) && this.stacks[i] > maxStack) {
            maxStack = this.stacks[i];
         }
      }

      return maxStack;
   }

   public final int getActivePlayerCount() {
      int count = this.nWay;

      for(int i = 0; i < this.nWay; ++i) {
         if (solver.Equity.isBitSet(this.foldedBits, i)) {
            --count;
         }
      }

      return count;
   }

   public final int getFirstActivePlayer() {
      for(int i = 0; i < this.nWay; ++i) {
         if (!solver.Equity.isBitSet(this.foldedBits, i)) {
            return i;
         }
      }

      return -1;
   }

   public final int[] getActivePlayerIndices() {
      int[] indices = new int[this.getActivePlayerCount()];
      int count = 0;

      for(int i = 0; i < this.nWay; ++i) {
         if (!solver.Equity.isBitSet(this.foldedBits, i)) {
            indices[count++] = i;
         }
      }

      return indices;
   }

   public final boolean isActionAllowed(int actionType) {
      if (this.gameStage == 4) {
         return false;
      } else if (actionType == 1) {
         return true;
      } else {
         int maxBet = this.getMaxBet();
         if (actionType == 0 && this.bets[this.firstPlayerToAct] < maxBet && this.bets[this.firstPlayerToAct] < this.stacks[this.firstPlayerToAct]) {
            return true;
         } else if (actionType == 0) {
            return false;
         } else {
            GameState lastAggressor;
            if (!((lastAggressor = this.findLastValidAggressor()).lastAggressorIndex < 0 ? true : (lastAggressor.parentNode.gameStage < this.gameStage ? true : lastAggressor.bets[lastAggressor.lastAggressorIndex] > this.bets[this.firstPlayerToAct]))) {
               return false;
            } else {
               GameState newState;
               int maxBetInLoop;
               int playerIdx;
               GameState tempState;
               int activeOpponentCount;
               if (actionType == 3) {
                  if (AnalysisPanel.i == 0) {
                     return this.getMaxBet() < this.stacks[this.firstPlayerToAct];
                  } else {
                     GameState allInState = new GameState(this, actionType);
                     tempState = new GameState(this, solver.BetType.POT);
                     if (allInState.bets[this.firstPlayerToAct] <= tempState.bets[this.firstPlayerToAct] && this.getMaxBet() < this.stacks[this.firstPlayerToAct]) {
                        newState = this;
                        activeOpponentCount = 0;
                        maxBetInLoop = this.getMaxBet();

                        for(playerIdx = 0; playerIdx < newState.nWay; ++playerIdx) {
                           if (!solver.Equity.isBitSet(newState.foldedBits, playerIdx) && newState.stacks[playerIdx] > maxBetInLoop) {
                              ++activeOpponentCount;
                           }
                        }

                        if (activeOpponentCount > 1) {
                           return true;
                        }
                     }

                     return false;
                  }
               } else if (actionType == 6) {
                  return this.raiseCount < 4;
               } else {
                  int currentPlayer = this.firstPlayerToAct;
                  tempState = new GameState(this, actionType);
                  newState = this;
                  activeOpponentCount = 0;
                  maxBetInLoop = 0;

                  for(playerIdx = 0; playerIdx < newState.bets.length; ++playerIdx) {
                     if (newState.bets[playerIdx] > activeOpponentCount) {
                        maxBetInLoop = activeOpponentCount;
                        activeOpponentCount = newState.bets[playerIdx];
                     } else if (newState.bets[playerIdx] > maxBetInLoop) {
                        maxBetInLoop = newState.bets[playerIdx];
                     }
                  }

                  int raiseDifference = activeOpponentCount - maxBetInLoop;
                  if (tempState.bets[this.firstPlayerToAct] - maxBet < raiseDifference) {
                     return false;
                  } else if (tempState.bets[currentPlayer] >= this.stacks[currentPlayer]) {
                     return false;
                  } else {
                     return true;
                  }
               }
            }
         }
      }
   }

   private GameState findLastActionNodeForStage(int stage) {
      GameState node = this;

      while(node.nodeType >= 0) {
         if (node.parentNode.gameStage != stage) {
            return node;
         }

         if (node.nodeType != 0 && node.nodeType != 1) {
            return node;
         }

         node = node.parentNode;
      }

      return node;
   }

   private GameState findLastValidAggressor() {
      GameState node = this;


      GameState lastActionNode;
      while((lastActionNode = node.findLastActionNodeForStage(node.gameStage)).parentNode != null) {
         int betIncrement;
         if ((betIncrement = lastActionNode.bets[lastActionNode.parentNode.firstPlayerToAct] - lastActionNode.parentNode.getMaxBet()) <= 0) {
            return lastActionNode;
         }

         GameState prevActionNode;
         int prevBetIncrement;
         if ((prevActionNode = lastActionNode.parentNode.findLastActionNodeForStage(node.gameStage)).parentNode == null) {
            prevBetIncrement = node.getBigBlind();
         } else {
            prevBetIncrement = prevActionNode.bets[prevActionNode.parentNode.firstPlayerToAct] - prevActionNode.parentNode.getMaxBet();
         }

         if (prevBetIncrement <= betIncrement) {
            return lastActionNode;
         }

         node = prevActionNode;
      }

      return lastActionNode;
   }

   public final boolean isBetSizeValid(double betRatio, int actionType) {
      int targetBet = (int)((double)this.stacks[this.firstPlayerToAct] * betRatio);
      int currentPlayer = this.firstPlayerToAct;
      GameState testState = new GameState(this, actionType);
      if (actionType == 1 && testState.bets[currentPlayer] == testState.stacks[currentPlayer]) {
         return false;
      } else if (testState.bets[currentPlayer] >= targetBet) {
         return AnalysisPanel.i == 0 || !solver.BetType.isBetAction(actionType) || this.isActionAllowed(3);
      } else {
         return false;
      }
   }

   public final boolean isBetSizeValid(double betRatio, BetType betType) {
      return this.isBetSizeValid(betRatio, betType.intValue());
   }

   private boolean isTerminalAllIn() {
      int remainingPlayers = this.nWay;
      int activePlayers = 0;
      int allInCount = 0;
      int notAllInIndex = -1;

      for(int i = 0; i < this.nWay; ++i) {
         if (solver.Equity.isBitSet(this.foldedBits, i)) {
            --remainingPlayers;
         } else {
            ++activePlayers;
            if (this.bets[i] >= this.stacks[i]) {
               ++allInCount;
            } else {
               notAllInIndex = i;
            }
         }
      }

      if (remainingPlayers <= 0) {
         return true;
      } else if (notAllInIndex == -1) {
         return true;
      } else if (activePlayers > 1 && activePlayers - allInCount == 1 && this.bets[notAllInIndex] >= this.getMaxBet()) {
         return true;
      } else {
         return false;
      }
   }

   private boolean allActivePlayersChecked() {
      for(int i = 0; i < this.nWay; ++i) {
         if (!solver.Equity.isBitSet(this.foldedBits, i) && this.bets[i] != this.stacks[i] && !solver.Equity.isBitSet(this.checkedBits, i)) {
            return false;
         }
      }

      return true;
   }

   private boolean isStreetComplete() {
      if (!this.allActivePlayersChecked()) {
         return false;
      } else {
         int maxBet = this.getMaxBet();

         for(int i = 0; i < this.bets.length; ++i) {
            if (!solver.Equity.isBitSet(this.foldedBits, i) && this.bets[i] != this.stacks[i] && this.bets[i] != maxBet) {
               return false;
            }
         }

         return true;
      }
   }

   public final boolean isHandOver() {
      int activePlayers = 0;

      for(int i = 0; i < this.nWay; ++i) {
         if (!solver.Equity.isBitSet(this.foldedBits, i)) {
            ++activePlayers;
         }
      }

      if (activePlayers <= 1) {
         return true;
      } else {
         return false;
      }
   }

   private static int[] copyIntArray(int[] source) {
      int[] copy = new int[source.length];

      for(int i = 0; i < source.length; ++i) {
         copy[i] = source[i];
      }

      return copy;
   }

   public final int getNegatedAggressorBet() {
      return -this.bets[this.lastAggressorIndex];
   }

   public static int[] eligiblePlayers(int[] stacks) {
      int count = 0;

      // Count the number of elements less than 99999
      for (int i = 0; i < stacks.length; i++) {
         if (stacks[i] < 99999) {
            count++;
         }
      }

      // Create an array to store the indices
      int[] indices = new int[count];
      int index = 0;

      // Fill the array with indices of elements less than 99999
      for (int i = 0; i < stacks.length; i++) {
         if (stacks[i] < 99999) {
            indices[index] = i;
            index++;
         }
      }

      return indices;
   }

   public static List<Integer> findLowestIndexes(int[] values) {
      int minValue = values[0];
      List<Integer> indexes = new ArrayList<>();

      for (int i = 0; i < values.length; ++i) {
         if (values[i] < minValue) {
            minValue = values[i];
            indexes.clear();
            indexes.add(i);
         } else if (values[i] == minValue) {
            indexes.add(i);
         }
      }
      return indexes;
   }

   public final double[] computeSidePots(int capAmount, double rakeRatio, double[] potArray, int[] stackArray, int[] stackCap, boolean[] processedArray) {
      int i;

      for(i = 0; i < stackArray.length; ++i) {
         processedArray[i] = false;
         potArray[i] = (double)this.bets[i];
         if (solver.Equity.isBitSet(this.foldedBits, i)) {
            stackArray[i] = 999999;
         } else {
            stackArray[i] = stackCap[i];
         }
      }

      double deadMoneyRemaining = (double)this.deadMoney[0];

      for(int nextShowdown = this.findNextShowdownIndex(stackArray, processedArray); nextShowdown != -1; nextShowdown = this.findNextShowdownIndex(stackArray, processedArray)) {
         int minActiveValue = findMinActiveValue(stackArray, processedArray);
         i = countActiveAtValue(stackArray, processedArray, minActiveValue);
         processedArray[nextShowdown] = true;
         double sidePot = 0.0D;

         for(int playerIdx = 0; playerIdx < stackArray.length; ++playerIdx) {
            if (!processedArray[playerIdx] && stackArray[playerIdx] != minActiveValue) {
               double contribution = Math.min(potArray[nextShowdown], potArray[playerIdx]) / (double)i;
               potArray[playerIdx] -= contribution;
               sidePot += contribution;
            }
         }

         sidePot += deadMoneyRemaining / (double)i;
         deadMoneyRemaining -= deadMoneyRemaining / (double)i;
         potArray[nextShowdown] += sidePot;
      }

      for(int playerIdx = 0; playerIdx < potArray.length; ++playerIdx) {
         if (this.bets[playerIdx] > capAmount) {
            potArray[playerIdx] -= (double)(this.bets[playerIdx] - capAmount);
            potArray[playerIdx] -= potArray[playerIdx] * rakeRatio;
            potArray[playerIdx] -= (double)capAmount;
         } else {
            potArray[playerIdx] -= potArray[playerIdx] * rakeRatio;
            potArray[playerIdx] -= (double)this.bets[playerIdx];
         }
      }

      return potArray;
   }

   final double[] computeFinalStacks(double[] stackArray, int[] stackCap, int[] stackHand, boolean[] processedArray) {
      int i;
      for(i = 0; i < stackCap.length; ++i) {
         processedArray[i] = false;
         stackArray[i] = (double)this.bets[i];
         if (solver.Equity.isBitSet(this.foldedBits, i)) {
            stackCap[i] = 9999;
         } else {
            stackCap[i] = stackHand[i];
         }
      }

      double deadMoneyRemaining = (double)this.deadMoney[0];

      for(i = this.findNextShowdownIndex(stackCap, processedArray); i != -1; i = this.findNextShowdownIndex(stackCap, processedArray)) {
         int minValue = findMinActiveValue(stackCap, processedArray);
         int activeCount = countActiveAtValue(stackCap, processedArray, minValue);
         processedArray[i] = true;
         double sidePot = 0.0D;

         for(int playerIdx = 0; playerIdx < stackCap.length; ++playerIdx) {
            if (!processedArray[playerIdx] && stackCap[playerIdx] != minValue) {
               double contribution = Math.min(stackArray[i], stackArray[playerIdx]) / (double)activeCount;
               stackArray[playerIdx] -= contribution;
               sidePot += contribution;
            }
         }

         sidePot += deadMoneyRemaining / (double)activeCount;
         deadMoneyRemaining -= deadMoneyRemaining / (double)activeCount;
         stackArray[i] += sidePot;
      }

      for(int playerIdx = 0; playerIdx < stackArray.length; ++playerIdx) {
         stackArray[playerIdx] += (double)(this.stacks[playerIdx] - this.bets[playerIdx]);
      }

      return stackArray;
   }

   public final double clampedProduct(double factor1, double factor2, double maxValue) {
      if (this.parentNode.gameStage == 0 && this.isHandOver() && !solver.Equity.isBitSet(GameSettings.rakeFlags, 0)) {
         return 0.0D;
      } else {
         double product;
         if ((product = factor1 * factor2) > maxValue) {
            product = maxValue;
         }

         return product;
      }
   }

   public static double clampedRatio(double dividend, double ratio, double maxValue) {
      if (dividend < 1.0E-8D) {
         return 0.0D;
      } else {
         return dividend * ratio > maxValue ? maxValue / dividend : ratio;
      }
   }

   private int findNextShowdownIndex(int[] stackValues, boolean[] processed) {
      int nextIndex = -1;

      for (int i = 0; i < stackValues.length; ++i) {
         if (!processed[i]) {
            if (nextIndex == -1 || stackValues[i] < stackValues[nextIndex] || (stackValues[i] == stackValues[nextIndex] && this.bets[i] < this.bets[nextIndex])) {
               nextIndex = i;
            }
         }
      }

      return nextIndex;
   }

   private static int findMinActiveValue(int[] stackValues, boolean[] processed) {
      int minValue = 9999990;

      for(int i = 0; i < stackValues.length; ++i) {
         if (!processed[i] && stackValues[i] < minValue) {
            minValue = stackValues[i];
         }
      }

      return minValue;
   }

   private static int countActiveAtValue(int[] stackValues, boolean[] processed, int targetValue) {
      int count = 0;

      for(int i = 0; i < stackValues.length; ++i) {
         if (!processed[i] && stackValues[i] == targetValue) {
            ++count;
         }
      }

      return count;
   }

   public final boolean betSizesEqual(int actionType1, int actionType2) {
      if (actionType1 != 0 && actionType2 != 0) {
         if (actionType1 == actionType2) {
            return true;
         } else {
            GameState state1 = new GameState(this, actionType1);
            GameState state2 = new GameState(this, actionType2);
            return state1.bets[this.firstPlayerToAct] == state2.bets[this.firstPlayerToAct];
         }
      } else {
         return actionType1 == actionType2;
      }
   }

   public final boolean equals(Object other) {
      return this.hasSameState((GameState)other);
   }

   public final boolean hasSameState(GameState other) {
      GameState current = this;

      while(current != other) {
         if (other != null && current.nodeType == other.nodeType) {
            if (current.stateHash == other.stateHash) {
               if (current.parentNode != null && other.parentNode != null) {
                  other = other.parentNode;
                  current = current.parentNode;
                  continue;
               }

               if (current.parentNode == other.parentNode) {
                  return true;
               }

               return false;
            }

            return false;
         }

         return false;
      }

      return true;
   }

   public final int hashCode() {
      return (int)(this.stateHash ^ this.stateHash >>> 32);
   }

   public final void pushActionTypes(Stack stack) {
      GameState current = this;

      while(current.parentNode != null) {
         stack.push(current.nodeType);
         current = current.parentNode;
      }

   }

   public final int getBigBlind() {
      GameState rootNode = this.getRootNode();

      if (rootNode.gameStage > 0) {
         return 2000;
      } else {
         int minBlind = Integer.MAX_VALUE;
         int secondMinBlind = Integer.MAX_VALUE;
         int[] betsArray;
         int betsLength = (betsArray = rootNode.bets).length;

         for (int i = 0; i < betsLength; ++i) {
            int bet;
            if ((bet = betsArray[i]) != 0) {
               if (bet < minBlind) {
                  secondMinBlind = minBlind;
                  minBlind = bet;
               } else if (bet < secondMinBlind) {
                  secondMinBlind = bet;
               }
            }
         }

         return (secondMinBlind == Integer.MAX_VALUE) ? minBlind : secondMinBlind;
      }
   }

   public final int playersVpipedAtNode() {
      GameState rootNode = this.getRootNode();
      int[] rootBets = rootNode.bets;
      int count = 0;

      // Compare each player's current bet with their root bet
      for (int i = 0; i < this.bets.length; i++) {
         if (this.bets[i] > rootBets[i]) {
            count++;
         }
      }

      return count;
   }

   private GameState getRootNode() {
      if (rootNodeCache == null) {
         GameState node = this;
         while (node.parentNode != null) {
            node = node.parentNode;
         }
         rootNodeCache = node;
      }
      return rootNodeCache;
   }

   public int countZeros(int[] array) {
      return (int) Arrays.stream(array).filter(value -> value == 0).count();
   }
}
