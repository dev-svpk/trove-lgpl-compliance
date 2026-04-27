package solver;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import java.io.File;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableRowSorter;

public final class FlopNE {
   static HandRangeReader handRangeReader;
   static long version;
   private static HashMap nodeActionCache;
   private static card[][][] playerHands;
   static byte[][] playerCardBytes;
   private static double[][] playerEquities;
   private static double[][] playerStrategyWeights;
   static card[] boardCardsArray;
   private static int totalNodeCount;
   private static int terminalScenarioCount;
   static int isoLevel;
   static ViewSettingsManager viewSettings;
   static boolean usePrecomputedEquity;
   static File savedFile;
   static collections.LongIntHashMap preflopHandBucketMap;
   static double[] icm;
   private static collections.LongIntHashMap[] streetBucketMaps;
   static collections.IntIntHashMap flopTexture;
   static collections.IntIntHashMap turnTexture;
   static collections.IntIntHashMap riverTexture;
   private static collections.LongIntHashMap riverIndexMap;
   private static TIntIntHashMap[] riverBucketLookup;
   private static HashMap gameStateToNodeId;
   static collections.LongIntHashMap[] postflopBucketMaps;
   static int flopBuckets;
   static int turnBuckets;
   static int riverBuckets;
   static int turnTextureType; //0 - None; 1 - Large; 2 - Perfect; 3 - Small; 4 - Medium
   static int riverTextureType; //0 - None; 1 - Large; 2 - Small;
   static boolean isShortDeck;
   static boolean useChipEV;
   static double[][][] cfrTables;
   static double[][] avg;
   static boolean[] hasEV;
   static double[] evs;
   public static int[][] cachedEVs;

   public static int[] noAdjustment;
   public static double bbAmount;
   static long[] evIterationCounts;
   static int[] nodeActionCounts;
   static FilterButtonListener adjustmentListener;
   private static int[] cardRunoutOffsets;
   private static int currentNodeId;
   static int[] buckets;
   static long iterations;
   static boolean skipBucketing;
   static long iterationScaleCount;
   static long solveStartTime;
   static GameState gameState;
   static int threadCount;
   static int avgStreets;
   static int evStreets;
   static int maxSolveStreet;
   private static int terminalNodeCounter;
   private static int[] streetActionCounts;
   static long cfrTableMemoryBytes;
   private static int[] suitNormBuffer;
   private static int[] cardValueBuffer;
   private static int[] suitIndexBuffer;
   private static card[] tempCardArray;
   private static TLongDoubleHashMap equityCachePrimary;
   private static TLongDoubleHashMap equityCacheSecondary;
   private static gnu.wrapper.set.LongSet troveCollection;
   private static File savedRunsDirectory;

   public static int[] squidMarkers;
   public static double squidPrice;
   public static int squidTotalAmount;
   public static final int MAX_MULTIPLIERS = 10;
   public static int[] multiplierQualifiers = new int[MAX_MULTIPLIERS];
   public static int[] multiplierAmounts = new int[MAX_MULTIPLIERS];
   public static int firstRoundAmount = 1;

   static {
      version = solver.MainTabbedPane.version;
      nodeActionCache = new HashMap();
      usePrecomputedEquity = false;
      gameStateToNodeId = new HashMap();
      flopBuckets = 30;
      turnBuckets = 30;
      riverBuckets = 30;
      turnTextureType = 1;
      riverTextureType = 1;
      useChipEV = true;
      iterations = 0L;
      skipBucketing = false;
      iterationScaleCount = 0L;
      threadCount = Math.min(20, Math.max(Runtime.getRuntime().availableProcessors() - 1, 2));
      avgStreets = 1;
      evStreets = 1;
      maxSolveStreet = 5;
      terminalNodeCounter = 0;
      cfrTableMemoryBytes = 0L;
      suitNormBuffer = new int[4];
      cardValueBuffer = new int[9];
      suitIndexBuffer = new int[9];
      tempCardArray = solver.card.createCardArray(9);
      equityCachePrimary = new TLongDoubleHashMap(1000, 0.75F, -1L, -1.0D);
      equityCacheSecondary = new TLongDoubleHashMap();
      troveCollection = new gnu.wrapper.set.LongSet();

      squidMarkers = new int[20];
      Arrays.fill(squidMarkers, 0);
      squidPrice = 0;
      squidTotalAmount = 0;
      multiplierQualifiers = new int[MAX_MULTIPLIERS];
      multiplierAmounts = new int[MAX_MULTIPLIERS];
      Arrays.fill(multiplierQualifiers, 0);
      Arrays.fill(multiplierAmounts, 0);
   }

   private static card[] addCardToArray(card[] cards, card cardToAppend) {
      card[] result = new card[cards.length + 1];

      for(int i = 0; i < cards.length; ++i) {
         result[i] = cards[i];
      }

      result[cards.length] = cardToAppend;
      return result;
   }

   private static card[] combine2CardsWithArray(card[] holeCards, card[] additionalCards) {
      card[] result;
      (result = new card[additionalCards.length + 2])[0] = holeCards[0];
      result[1] = holeCards[1];

      for(int i = 0; i < additionalCards.length; ++i) {
         result[i + 2] = additionalCards[i];
      }

      return result;
   }

   public static int[] computePostflopBucketsForOmahaRange(ViewSettingsManager viewSettings, OmahaHandRange handRange, int street, card[] boardCards, int isoLevel) throws InterruptedException {
      (handRange = new OmahaHandRange(handRange)).removeKnownCards(boardCards);
      return computePostflopBuckets(viewSettings == null ? null : viewSettings.copy(), handRange.toByteArray(), handRange.getNonZeroIndices(), street, new collections.LongIntHashMap(), boardCards, viewSettings == null ? isoLevel : 1);
   }

   public static int[] computePostflopBuckets(ViewSettingsManager viewSettings, byte[] cardBytes, int[] handWeights, int street, collections.LongIntHashMap bucketMap, card[] boardCards, int isoLevel) throws InterruptedException {
      // Use 5-card logic if gameType == 4
      if (AnalysisPanel.is5Card()) {
         return computePostflopBuckets5c(viewSettings, cardBytes, handWeights, street, bucketMap, boardCards, isoLevel);
      }

      int flopBucketCount = 0;
      int turnBucketCount = 0;
      int riverBucketCount = 0;
      int[] sortedCardValues = new int[9];
      int[] cardValueBuffer = new int[9];
      int[] suitCountBuffer = new int[4];
      HandFilterParser handFilter = null;
      HashUtil hashUtil = null;
      if (viewSettings != null) {
         handFilter = new HandFilterParser();
         viewSettings.evaluateFilters(handFilter, boardCards);
         hashUtil = ViewSettingsManager.newBucketMap();
      }

      int filterBucketCount = viewSettings == null ? 0 : viewSettings.countBuckets(street);

      int loopIndex;
      for(loopIndex = 0; loopIndex < boardCards.length; ++loopIndex) {
         cardValueBuffer[loopIndex + 4] = boardCards[loopIndex].getFullDeckIndex52();
      }

      int bucketIndex;
      for(loopIndex = 0; loopIndex < cardBytes.length; loopIndex += 4) {
         cardValueBuffer[0] = cardBytes[loopIndex];
         cardValueBuffer[1] = cardBytes[loopIndex + 1];
         cardValueBuffer[2] = cardBytes[loopIndex + 2];
         cardValueBuffer[3] = cardBytes[loopIndex + 3];
         long normalizedHandKey = OmahaHandNormalizer.normalizeMulti(cardValueBuffer, sortedCardValues, boardCards.length + 4, suitCountBuffer, isoLevel);
         if (!bucketMap.containsKey(normalizedHandKey)) {
            if (filterBucketCount > 0) {
               bucketIndex = viewSettings.computeFilterBucketIndex(handWeights[loopIndex / 4], street, hashUtil);
               bucketMap.put(normalizedHandKey, bucketIndex);
               flopBucketCount = Math.max(bucketIndex + 1, flopBucketCount);
            } else {
               bucketMap.put(normalizedHandKey, flopBucketCount++);
            }
         }
      }

      loopIndex = 4 - street;
      if (maxSolveStreet < loopIndex) {
         loopIndex = maxSolveStreet;
      }

      if (loopIndex >= 2) {
         int totalCardCount = boardCards.length + 4;
         List remainingCards;
         if (AnalysisPanel.gameType == 3) {
            remainingCards = solver.CardArrays.getRestCardsShortdeck(boardCards);
         } else {
            remainingCards = solver.CardArrays.getRestCardsFullDeck(boardCards);
         }
         cardRunoutOffsets = new int[remainingCards.size()];
         bucketIndex = 0;
         int nextStreet = street + 1;
         filterBucketCount = 0;
         if (viewSettings != null) {
            hashUtil = ViewSettingsManager.newBucketMap();
            filterBucketCount = viewSettings.countBuckets(nextStreet);
         }

         card[] extendedBoard = (card[])Arrays.copyOf(boardCards, boardCards.length + 1);
         Iterator cardIterator = remainingCards.iterator();

         card currentCard;
         while(cardIterator.hasNext()) {
            currentCard = (card)cardIterator.next();
            extendedBoard[boardCards.length] = currentCard;
            cardRunoutOffsets[bucketIndex++] = turnBucketCount;
            cardValueBuffer[totalCardCount] = currentCard.getFullDeckIndex52();
            boolean needsFilterInit = true;

            for(int handIndex = 0; handIndex < cardBytes.length; handIndex += 4) {
               if (cardBytes[handIndex] != cardValueBuffer[totalCardCount] && cardBytes[handIndex + 1] != cardValueBuffer[totalCardCount] && cardBytes[handIndex + 2] != cardValueBuffer[totalCardCount] && cardBytes[handIndex + 3] != cardValueBuffer[totalCardCount]) {
                  cardValueBuffer[0] = cardBytes[handIndex];
                  cardValueBuffer[1] = cardBytes[handIndex + 1];
                  cardValueBuffer[2] = cardBytes[handIndex + 2];
                  cardValueBuffer[3] = cardBytes[handIndex + 3];
                  long turnHandKey = OmahaHandNormalizer.normalizeMulti(cardValueBuffer, sortedCardValues, totalCardCount + 1, suitCountBuffer, isoLevel);
                  if (!bucketMap.containsKey(turnHandKey)) {
                     if (filterBucketCount > 0) {
                        if (needsFilterInit) {
                           viewSettings.evaluateFilters(handFilter, extendedBoard);
                           ViewSettingsManager.clearBucketMap(hashUtil);
                        }

                        int assignedBucket = viewSettings.computeFilterBucketIndex(handWeights[handIndex / 4], nextStreet, hashUtil);
                        bucketMap.put(turnHandKey, assignedBucket);
                        turnBucketCount = Math.max(assignedBucket + 1, turnBucketCount);
                        needsFilterInit = false;
                     } else {
                        bucketMap.put(turnHandKey, turnBucketCount++);
                     }
                  }
               }
            }
         }

         if (loopIndex >= 3) {
            extendedBoard = new card[]{boardCards[0], boardCards[1], boardCards[2], null, null};
            filterBucketCount = 0;
            if (viewSettings != null) {
               filterBucketCount = viewSettings.countBuckets(street + 2);
               hashUtil = ViewSettingsManager.newBucketMap();
            }

            if (AnalysisPanel.gameType == 3) {
               cardIterator = solver.CardArrays.getRestCardsShortdeck(boardCards).iterator();
            } else {
               cardIterator = solver.CardArrays.getRestCardsFullDeck(boardCards).iterator();
            }


            while(cardIterator.hasNext()) {
               currentCard = (card)cardIterator.next();
               card[] turnBoard = addCardToArray(boardCards, currentCard);
               cardValueBuffer[7] = currentCard.getFullDeckIndex52();
               extendedBoard[3] = currentCard;
               Iterator riverCardIterator;

               if (AnalysisPanel.gameType == 3) {
                  riverCardIterator = solver.CardArrays.getRestCardsShortdeck(turnBoard).iterator();
               } else {
                  riverCardIterator = solver.CardArrays.getRestCardsFullDeck(turnBoard).iterator();
               }



               while(riverCardIterator.hasNext()) {
                  currentCard = (card)riverCardIterator.next();
                  if (solver.SolverRunner.stopRequested) {
                     throw new InterruptedException();
                  }

                  extendedBoard[4] = currentCard;
                  cardValueBuffer[8] = currentCard.getFullDeckIndex52();
                  long boardHashKey = OmahaHandNormalizer.hashBoardRiverLo(cardValueBuffer, sortedCardValues, suitCountBuffer, isoLevel);
                  boolean riverNeedsFilterInit = true;

                  for(totalCardCount = 0; totalCardCount < cardBytes.length; totalCardCount += 4) {
                     if (cardBytes[totalCardCount] != cardValueBuffer[7] && cardBytes[totalCardCount] != cardValueBuffer[8] && cardBytes[totalCardCount + 1] != cardValueBuffer[7] && cardBytes[totalCardCount + 1] != cardValueBuffer[8] && cardBytes[totalCardCount + 2] != cardValueBuffer[7] && cardBytes[totalCardCount + 2] != cardValueBuffer[8] && cardBytes[totalCardCount + 3] != cardValueBuffer[7] && cardBytes[totalCardCount + 3] != cardValueBuffer[8]) {
                        cardValueBuffer[0] = cardBytes[totalCardCount];
                        cardValueBuffer[1] = cardBytes[totalCardCount + 1];
                        cardValueBuffer[2] = cardBytes[totalCardCount + 2];
                        cardValueBuffer[3] = cardBytes[totalCardCount + 3];
                        long riverHandKey = OmahaHandNormalizer.hashWithBoardDigest(cardValueBuffer, sortedCardValues, suitCountBuffer, boardHashKey, isoLevel);
                        if (!bucketMap.containsKey(riverHandKey)) {
                           if (filterBucketCount > 0) {
                              if (riverNeedsFilterInit) {
                                 viewSettings.evaluateFilters(handFilter, extendedBoard);
                                 ViewSettingsManager.clearBucketMap(hashUtil);
                              }

                              loopIndex = viewSettings.computeFilterBucketIndex(handWeights[totalCardCount / 4], 3, hashUtil);
                              bucketMap.put(riverHandKey, loopIndex);
                              riverBucketCount = Math.max(loopIndex + 1, riverBucketCount);
                              riverNeedsFilterInit = false;
                           } else {
                              bucketMap.put(riverHandKey, riverBucketCount++);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      System.lineSeparator();
      bucketMap.trim();
      if (street == 1) {
         return new int[]{flopBucketCount, turnBucketCount, riverBucketCount};
      } else {
         return street == 2 ? new int[]{0, flopBucketCount, turnBucketCount} : new int[]{0, 0, flopBucketCount};
      }
   }

   // 5-card version of the method above
   public static int[] computePostflopBuckets5c(ViewSettingsManager viewSettings, byte[] cardBytes, int[] handWeights, int street, collections.LongIntHashMap bucketMap, card[] boardCards, int isoLevel) throws InterruptedException {
      int flopBucketCount = 0;
      int turnBucketCount = 0;
      int riverBucketCount = 0;
      int[] sortedCardValues = new int[10];
      int[] cardValueBuffer = new int[10];
      int[] suitCountBuffer = new int[4];
      HandFilterParser handFilter = null;
      HashUtil hashUtil = null;
      if (viewSettings != null) {
         handFilter = new HandFilterParser();
         viewSettings.evaluateFilters(handFilter, boardCards);
         hashUtil = ViewSettingsManager.newBucketMap();
      }

      int filterBucketCount = viewSettings == null ? 0 : viewSettings.countBuckets(street);

      int loopIndex;
      for(loopIndex = 0; loopIndex < boardCards.length; ++loopIndex) {
         cardValueBuffer[loopIndex + 5] = boardCards[loopIndex].getFullDeckIndex52();
      }

      int bucketIndex;
      for(loopIndex = 0; loopIndex < cardBytes.length; loopIndex += 5) {
         cardValueBuffer[0] = cardBytes[loopIndex];
         cardValueBuffer[1] = cardBytes[loopIndex + 1];
         cardValueBuffer[2] = cardBytes[loopIndex + 2];
         cardValueBuffer[3] = cardBytes[loopIndex + 3];
         cardValueBuffer[4] = cardBytes[loopIndex + 4];
         long normalizedHandKey = OmahaHandNormalizer.normalizeMulti5c(cardValueBuffer, sortedCardValues, boardCards.length + 5, suitCountBuffer, isoLevel);
         if (!bucketMap.containsKey(normalizedHandKey)) {
            if (filterBucketCount > 0) {
               bucketIndex = viewSettings.computeFilterBucketIndex(handWeights[loopIndex / 5], street, hashUtil);
               bucketMap.put(normalizedHandKey, bucketIndex);
               flopBucketCount = Math.max(bucketIndex + 1, flopBucketCount);
            } else {
               bucketMap.put(normalizedHandKey, flopBucketCount++);
            }
         }
      }

      loopIndex = 4 - street;
      if (maxSolveStreet < loopIndex) {
         loopIndex = maxSolveStreet;
      }

      if (loopIndex >= 2) {
         int totalCardCount = boardCards.length + 5;
         List remainingCards;
         remainingCards = solver.CardArrays.getRestCardsFullDeck(boardCards);
         cardRunoutOffsets = new int[remainingCards.size()];
         bucketIndex = 0;
         int nextStreet = street + 1;
         filterBucketCount = 0;
         if (viewSettings != null) {
            hashUtil = ViewSettingsManager.newBucketMap();
            filterBucketCount = viewSettings.countBuckets(nextStreet);
         }

         card[] extendedBoard = (card[])Arrays.copyOf(boardCards, boardCards.length + 1);
         Iterator cardIterator = remainingCards.iterator();

         card currentCard;
         while(cardIterator.hasNext()) {
            currentCard = (card)cardIterator.next();
            extendedBoard[boardCards.length] = currentCard;
            cardRunoutOffsets[bucketIndex++] = turnBucketCount;
            cardValueBuffer[totalCardCount] = currentCard.getFullDeckIndex52();
            boolean needsFilterInit = true;

            for(int handIndex = 0; handIndex < cardBytes.length; handIndex += 5) {
               if (cardBytes[handIndex] != cardValueBuffer[totalCardCount] && cardBytes[handIndex + 1] != cardValueBuffer[totalCardCount] && cardBytes[handIndex + 2] != cardValueBuffer[totalCardCount] && cardBytes[handIndex + 3] != cardValueBuffer[totalCardCount] && cardBytes[handIndex + 4] != cardValueBuffer[totalCardCount]) {
                  cardValueBuffer[0] = cardBytes[handIndex];
                  cardValueBuffer[1] = cardBytes[handIndex + 1];
                  cardValueBuffer[2] = cardBytes[handIndex + 2];
                  cardValueBuffer[3] = cardBytes[handIndex + 3];
                  cardValueBuffer[4] = cardBytes[handIndex + 4];
                  long turnHandKey = OmahaHandNormalizer.normalizeMulti5c(cardValueBuffer, sortedCardValues, totalCardCount + 1, suitCountBuffer, isoLevel);
                  if (!bucketMap.containsKey(turnHandKey)) {
                     if (filterBucketCount > 0) {
                        if (needsFilterInit) {
                           viewSettings.evaluateFilters(handFilter, extendedBoard);
                           ViewSettingsManager.clearBucketMap(hashUtil);
                        }

                        int assignedBucket = viewSettings.computeFilterBucketIndex(handWeights[handIndex / 5], nextStreet, hashUtil);
                        bucketMap.put(turnHandKey, assignedBucket);
                        turnBucketCount = Math.max(assignedBucket + 1, turnBucketCount);
                        needsFilterInit = false;
                     } else {
                        bucketMap.put(turnHandKey, turnBucketCount++);
                     }
                  }
               }
            }
         }

         if (loopIndex >= 3) {
            extendedBoard = new card[]{boardCards[0], boardCards[1], boardCards[2], null, null};
            filterBucketCount = 0;
            if (viewSettings != null) {
               filterBucketCount = viewSettings.countBuckets(street + 2);
               hashUtil = ViewSettingsManager.newBucketMap();
            }

            cardIterator = solver.CardArrays.getRestCardsFullDeck(boardCards).iterator();

            while(cardIterator.hasNext()) {
               currentCard = (card)cardIterator.next();
               card[] turnBoard = addCardToArray(boardCards, currentCard);
               cardValueBuffer[8] = currentCard.getFullDeckIndex52();
               extendedBoard[3] = currentCard;
               Iterator riverCardIterator;

               riverCardIterator = solver.CardArrays.getRestCardsFullDeck(turnBoard).iterator();

               while(riverCardIterator.hasNext()) {
                  currentCard = (card)riverCardIterator.next();
                  if (solver.SolverRunner.stopRequested) {
                     throw new InterruptedException();
                  }

                  extendedBoard[4] = currentCard;
                  cardValueBuffer[9] = currentCard.getFullDeckIndex52();
                  long boardHashKey = OmahaHandNormalizer.hashBoardRiverLo5c(cardValueBuffer, sortedCardValues, suitCountBuffer, isoLevel);
                  boolean riverNeedsFilterInit = true;

                  for(totalCardCount = 0; totalCardCount < cardBytes.length; totalCardCount += 5) {
                     if (cardBytes[totalCardCount]     != cardValueBuffer[8] && cardBytes[totalCardCount]     != cardValueBuffer[9] &&
                    	 cardBytes[totalCardCount + 1] != cardValueBuffer[8] && cardBytes[totalCardCount + 1] != cardValueBuffer[9] &&
                    	 cardBytes[totalCardCount + 2] != cardValueBuffer[8] && cardBytes[totalCardCount + 2] != cardValueBuffer[9] &&
                    	 cardBytes[totalCardCount + 3] != cardValueBuffer[8] && cardBytes[totalCardCount + 3] != cardValueBuffer[9] &&
                    	 cardBytes[totalCardCount + 4] != cardValueBuffer[8] && cardBytes[totalCardCount + 4] != cardValueBuffer[9])
                     {
                    	cardValueBuffer[0] = cardBytes[totalCardCount];
                        cardValueBuffer[1] = cardBytes[totalCardCount + 1];
                        cardValueBuffer[2] = cardBytes[totalCardCount + 2];
                        cardValueBuffer[3] = cardBytes[totalCardCount + 3];
                        cardValueBuffer[4] = cardBytes[totalCardCount + 4];
                        long riverHandKey = OmahaHandNormalizer.hashWithBoardDigest5c(cardValueBuffer, sortedCardValues, suitCountBuffer, boardHashKey, isoLevel);
                        if (!bucketMap.containsKey(riverHandKey)) {
                           if (filterBucketCount > 0) {
                              if (riverNeedsFilterInit) {
                                 viewSettings.evaluateFilters(handFilter, extendedBoard);
                                 ViewSettingsManager.clearBucketMap(hashUtil);
                              }

                              loopIndex = viewSettings.computeFilterBucketIndex(handWeights[totalCardCount / 5], 3, hashUtil);
                              bucketMap.put(riverHandKey, loopIndex);
                              riverBucketCount = Math.max(loopIndex + 1, riverBucketCount);
                              riverNeedsFilterInit = false;
                           } else {
                              bucketMap.put(riverHandKey, riverBucketCount++);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      System.lineSeparator();
      bucketMap.trim();
      if (street == 1) {
         return new int[]{flopBucketCount, turnBucketCount, riverBucketCount};
      } else {
         return street == 2 ? new int[]{0, flopBucketCount, turnBucketCount} : new int[]{0, 0, flopBucketCount};
      }
   }

   public static int[] computePostflopBucketsHoldem(card[][] holeCards, int street, card[] boardCards, collections.LongIntHashMap bucketMap) {
      int flopBucketCount = 0;
      int turnBucketCount = 0;
      int riverBucketCount = 0;
      card[] tempCardBuffer = solver.card.createCardArray(7);
      card[][] handsArray = holeCards;
      int numHands = holeCards.length;

      for(int handIdx = 0; handIdx < numHands; ++handIdx) {
         long normalizedKey = CardCombinations.normalizeAndHashHand(combine2CardsWithArray(handsArray[handIdx], boardCards), tempCardBuffer, new int[4]);
         if (!bucketMap.containsKey(normalizedKey)) {
            bucketMap.put(normalizedKey, flopBucketCount);
            ++flopBucketCount;
         }
      }

      int streetsRemaining = 4 - street;
      if (maxSolveStreet < streetsRemaining) {
         streetsRemaining = maxSolveStreet;
      }

      if (streetsRemaining >= 2) {
         int[] suitCountBuffer = new int[4];
         Iterator cardIterator;

         if (AnalysisPanel.gameType == 3) {
            cardIterator = solver.CardArrays.getRestCardsShortdeck(boardCards).iterator();
         } else {
            cardIterator = solver.CardArrays.getRestCardsFullDeck(boardCards).iterator();

         }

         card[] currentHand;
         while(cardIterator.hasNext()) {
            card turnCard = (card)cardIterator.next();
            card[][] handsRef = holeCards;
            int handsLen = holeCards.length;

            for(int i = 0; i < handsLen; ++i) {
               card[] hand = handsRef[i];
               if (!turnCard.cardEquals(hand[0]) && !turnCard.cardEquals(hand[1])) {
                  currentHand = boardCards;
                  card[] combinedCards;
                  (combinedCards = new card[boardCards.length + 1 + 2])[0] = hand[0];
                  combinedCards[1] = hand[1];

                  for(int boardIdx = 0; boardIdx < currentHand.length; ++boardIdx) {
                     combinedCards[boardIdx + 2] = currentHand[boardIdx];
                  }

                  combinedCards[currentHand.length + 2] = turnCard;
                  long turnKey = CardCombinations.normalizeAndHashHand(combinedCards, tempCardBuffer, suitCountBuffer);
                  if (!bucketMap.containsKey(turnKey)) {
                     bucketMap.put(turnKey, turnBucketCount);
                     ++turnBucketCount;
                  }
               }
            }
         }

         card[] fullBoard = new card[7];

         for(int boardIdx = 0; boardIdx < boardCards.length; ++boardIdx) {
            fullBoard[boardIdx + 2] = boardCards[boardIdx];
         }

         if (streetsRemaining >= 3) {
            Iterator turnIterator;// = solver.CardArrays.getRestCardsFullDeck(boardCards).iterator();

            if (AnalysisPanel.gameType == 3) {
               turnIterator = solver.CardArrays.getRestCardsShortdeck(boardCards).iterator();
            } else {
               turnIterator = solver.CardArrays.getRestCardsFullDeck(boardCards).iterator();
            }

            while(turnIterator.hasNext()) {
               card turnCard = (card)turnIterator.next();
               fullBoard[5] = turnCard;
               Iterator riverIterator;// = solver.CardArrays.getRestCardsFullDeck(addCardToArray(boardCards, turnCard)).iterator();
               if (AnalysisPanel.gameType == 3) {
                  riverIterator = solver.CardArrays.getRestCardsShortdeck(addCardToArray(boardCards, turnCard)).iterator();
               } else {
                  riverIterator = solver.CardArrays.getRestCardsFullDeck(addCardToArray(boardCards, turnCard)).iterator();
               }

               while(riverIterator.hasNext()) {
                  card riverCard = (card)riverIterator.next();
                  fullBoard[6] = riverCard;
                  card[][] handsRef = holeCards;
                  streetsRemaining = holeCards.length;

                  for(int handIdx = 0; handIdx < streetsRemaining; ++handIdx) {
                     currentHand = handsRef[handIdx];
                     if (!turnCard.cardEquals(currentHand[0]) && !turnCard.cardEquals(currentHand[1]) && !riverCard.cardEquals(currentHand[0]) && !riverCard.cardEquals(currentHand[1])) {
                        fullBoard[0] = currentHand[0];
                        fullBoard[1] = currentHand[1];
                        long riverKey = CardCombinations.normalizeAndHashHand(fullBoard, tempCardBuffer, suitCountBuffer);
                        if (!bucketMap.containsKey(riverKey)) {
                           bucketMap.put(riverKey, riverBucketCount);
                           ++riverBucketCount;
                        }
                     }
                  }
               }
            }
         }
      }

      if (street == 1) {
         return new int[]{flopBucketCount, turnBucketCount, riverBucketCount};
      } else {
         return street == 2 ? new int[]{0, flopBucketCount, turnBucketCount} : new int[]{0, 0, flopBucketCount};
      }
   }

   private static void initializeHasEVArray() {
      hasEV = new boolean[gameState.nWay << 2];


      for(int playerIndex = 0; playerIndex < gameState.nWay; ++playerIndex) {
         for(int street = 0; street < 4; ++street) {
            if (evStreets > street - gameState.gameStage) {
               hasEV[(playerIndex << 2) + street] = true;
            }
         }
      }

   }

   public static boolean isMemoryLow(long thresholdMB) {
      long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

      /*long totalM = Runtime.getRuntime().totalMemory();
      long freeM = Runtime.getRuntime().freeMemory();
      long maxM = Runtime.getRuntime().maxMemory();

      double freeMGb = freeM / (1024L * 1024L * 1024L);*/

      return Runtime.getRuntime().maxMemory() - usedMemory < 1048576L * thresholdMB;
   }

   private static void buildGameTree() throws InterruptedException {
      currentNodeId = 0;
      terminalNodeCounter = 0;
      countNodes(new GameState(gameState));
      terminalScenarioCount = terminalNodeCounter;
      totalNodeCount =currentNodeId + 1;
      UnsafeMemoryStorage.adjustedFlags = new boolean[currentNodeId + 1];
      avg = new double[currentNodeId + 1][];
      nodeActionCounts = new int[currentNodeId + 1];
      GameState[] gameScenarios = null;
      if (maxSolveStreet >= 4) {
         gameScenarios = new GameState[terminalScenarioCount];
      }

      currentNodeId = 0;
      terminalNodeCounter = 0;
      Arrays.fill(streetActionCounts = new int[4 * gameState.nWay], 0);
      System.lineSeparator();
      UnsafeMemoryStorage.releaseScenarios();
      UnsafeMemoryStorage.allocateNodes(totalNodeCount);
      iterationScaleCount = 0L;
      UnsafeMemoryStorage.initPackedNodeInfoStorage(terminalScenarioCount, gameState.nWay);
      buildGameTree(gameState, streetActionCounts, gameScenarios);

      System.gc();
      Thread.sleep(10L);
      int streetIdx;
      int playerIdx;
      if (!usePrecomputedEquity) {
         cfrTables = new double[4 * gameState.nWay][][];

         for(streetIdx = 0; streetIdx < 4; ++streetIdx) {
            Thread.sleep(10L);

            for(playerIdx = 0; playerIdx < gameState.nWay; ++playerIdx) {
               cfrTables[(playerIdx << 2) + streetIdx] = new double[buckets[(playerIdx << 2) + streetIdx]][streetActionCounts[(playerIdx << 2) + streetIdx]];
            }
         }

         calculateCfrTableMemory();
      }

      System.lineSeparator();
      if (maxSolveStreet >= 4) {
         UnsafeMemoryStorage.allocateScenarios(terminalScenarioCount);

         for(int scenarioIndex = 0; scenarioIndex < gameScenarios.length; ++scenarioIndex) {
            GameState currentScenario = gameScenarios[scenarioIndex];

            // Calculate pot and rake
            double totalPot = currentScenario.getTotalPot();
            double rakeAmount = currentScenario.clampedProduct(currentScenario.computeRake(), GameSettings.rakePercent, (double)GameSettings.rakeCap);

            // Store net pot (after rake) for this scenario
            UnsafeMemoryStorage.setPot(scenarioIndex, totalPot - rakeAmount);

            // Get indices of all active (non-folded) players
            int[] activePlayers = currentScenario.getActivePlayerIndices();

            // Store the negative bet amount of the first active player to remove from final pot
            UnsafeMemoryStorage.setSidePot(scenarioIndex, -currentScenario.bets[activePlayers[0]]);

            // NEW: Compute and store closest node ID for each player

            GameState node = currentScenario;
            while (node != null) {
               int nodeId = node.nodeId;
//                  System.out.println("Node " + nodeId + ", gameStage=" + node.gameStage +
//                          ", nodeType=" + node.nodeType +
//                          ", parent=" + (node.parentNode != null ? node.parentNode.nodeId : "null") +
//                          ", parentFirstToAct=" + (node.parentNode != null ? node.parentNode.firstPlayerToAct : "null"));

               if (nodeId > 0 && node.parentNode != null) {
                  int actingPlayer = node.parentNode.firstPlayerToAct;
                  //System.out.println("  -> Processing actingPlayer=" + actingPlayer);

                  if (actingPlayer >= 0 && actingPlayer < gameState.nWay) {
                     // CHANGE: Check for -1 instead of 0
                     if (UnsafeMemoryStorage.getPackedNodeInfo(scenarioIndex, actingPlayer) == -1) {
                        int[] parentActions = getAvailableActions(node.parentNode);
//                           System.out.println("  -> Parent actions: " + Arrays.toString(parentActions));

                        for (int i = 0; i < parentActions.length; i++) {
                           if (parentActions[i] == node.nodeType && node.nodeType == 0) {
//                                 int parentNodeM = node.parentNode.m;
//                                 parentNodeM = parentNodeM == 0 ? 1 : parentNodeM;
//                                 System.out.println("  -> Storing Player" + actingPlayer + " -> Parent" + parentNodeM + ", Pos" + i);
                              UnsafeMemoryStorage.setPackedNodeInfo(scenarioIndex, actingPlayer, node.nodeId, i);
                              break;
                           }
                        }
                     } else {
                        int currentPacked = UnsafeMemoryStorage.getPackedNodeInfo(scenarioIndex, actingPlayer);
                        //System.out.println("  -> Already stored for player " + actingPlayer + " (value: " + currentPacked + ")");
                     }
                  }
               } else {
//                     System.out.println("  -> Skipped: nodeId=" + nodeId + ", gameStage=" + node.gameStage +
//                             ", hasParent=" + (node.parentNode != null));
               }
               node = node.parentNode;
            }
         }
         if (FlopNE.gameState.gameStage == 0){
            bbAmount = Arrays.stream(FlopNE.gameState.bets).max().getAsInt();
         } else{
            bbAmount = 2000;
         }

         if (useChipEV) {
            adjustmentListener = new FilterButtonListener(1, 1);
            adjustmentListener.setAdjustmentEvs();
         } else {
            (adjustmentListener = new FilterButtonListener(gameState, icm, gameState.nWay)).initPayoffs(gameScenarios);
            adjustmentListener.setAdjustmentEvs();
         }
      }
//      debugDisplayCompactNodeInfo_AwRef(gameScenarios);
//      System.exit(0);
      System.lineSeparator();
   }

   // Debug display for the original UnsafeMemoryStorage implementation
   public static void debugDisplayCompactNodeInfo_AwRef(GameState[] scenarios) {
      System.out.println("=== COMPACT NODE INFO VIEW (UnsafeMemoryStorage Original) ===");

      int totalPlayers = gameState.nWay; // Use gameState.nWay for the original implementation

      // Header row
      System.out.printf("Scenario");
      for (int playerId = 0; playerId < totalPlayers; playerId++) {
         System.out.printf(" | P%d ", playerId);
      }
      System.out.println();

      // Separator
      System.out.printf("--------");
      for (int playerId = 0; playerId < totalPlayers; playerId++) {
         System.out.printf("-+----");
      }
      System.out.println();

      // Data rows
      for (int scenarioIndex = 0; scenarioIndex < Math.min(20, scenarios.length); scenarioIndex++) {
         System.out.printf("%8d", scenarioIndex);

         for (int playerId = 0; playerId < totalPlayers; playerId++) {
            // Get packed value from UnsafeMemoryStorage
            int packedValue = UnsafeMemoryStorage.getPackedNodeInfo(scenarioIndex, playerId);

            if (packedValue == -1) {
               System.out.printf(" |  - ");
            } else {
               // Extract parent node ID from packed value
               int nodeId = UnsafeMemoryStorage.getParentNodeId(packedValue);
               System.out.printf(" |%3d ", nodeId);
            }
         }
         System.out.println();
      }

      if (scenarios.length > 20) {
         System.out.printf("... (%d more scenarios not shown)%n", scenarios.length - 20);
      }
   }


   private static final int buildGameTree(GameState state, int[] streetActionCounts, GameState[] terminalScenarios) throws InterruptedException {
      if (currentNodeId % 100 == 0 && isMemoryLow(400L)) {
         System.lineSeparator();
         throw new OutOfMemoryError();
      } else if (solver.SolverRunner.stopRequested) {
         throw new InterruptedException();
      } else {
         ++currentNodeId;
         state.nodeId = currentNodeId;
         int currentNodeIdSnapshot = currentNodeId; // Store the current node ID for consistent debugging
         int savedNodeId = currentNodeId;
         int playerStreetIndex = (state.firstPlayerToAct << 2) + state.gameStage;
         UnsafeMemoryStorage.setPlayerStreetIndex((long)currentNodeId, playerStreetIndex);

         if (maxSolveStreet >= 4 && icm == null && state.isFold()) {
            UnsafeMemoryStorage.setAggressorBet((long)currentNodeId, state.getNegatedAggressorBet());
         }

         // Compute active players bitmask (for other uses)
         int[] activePlayers = state.getActivePlayerIndices();
         int activePlayersBitmask = 0;
         for(int i = 0; i < activePlayers.length; ++i) {
            activePlayersBitmask |= (1 << activePlayers[i]);
         }

         if (state.gameStage >= 4) {
            // Terminal node: winnable players are those still active
            UnsafeMemoryStorage.setWinnablePlayers(currentNodeId, activePlayersBitmask);
//            System.out.println("Branch " + currentNodeId + ": with bets " + Arrays.toString(state.bets) + " " + formatWinnablePlayers(activePlayersBitmask));
            UnsafeMemoryStorage.setPlayerStreetIndex((long)currentNodeId, -1);
            //payoff node
            UnsafeMemoryStorage.setNextNodeId((long)currentNodeId, currentNodeId);
            if (maxSolveStreet >= 4) {
               UnsafeMemoryStorage.setNodeOffset((long)currentNodeId, terminalNodeCounter);
               terminalScenarios[terminalNodeCounter] = state;
               ++terminalNodeCounter;
            }
            return 1;
         } else {
            List childStates = getChildStates(state);
            nodeActionCounts[currentNodeId] = childStates.size();
            UnsafeMemoryStorage.setNodeOffset((long)currentNodeId, streetActionCounts[playerStreetIndex]);
            streetActionCounts[playerStreetIndex] += nodeActionCounts[currentNodeId];
            if (hasEV[playerStreetIndex]) {
               streetActionCounts[playerStreetIndex] += 2;
            }

            if (maxSolveStreet > state.gameStage - gameState.gameStage) {
               playerStreetIndex = buckets[playerStreetIndex];
               if (state.gameStage < avgStreets + gameState.gameStage && !usePrecomputedEquity) {
                  avg[currentNodeId] = new double[playerStreetIndex * nodeActionCounts[currentNodeId]];
               }
               iterationScaleCount += (long)playerStreetIndex;
            }

            // Debug: Show current node info
            int[] availableActions = getAvailableActions(state);

            // Initialize winnable players mask
            int winnablePlayersMask = 0;

            playerStreetIndex = 0;
            int childTreeSize;
            int childIndex = 0;
            for(Iterator childIterator = childStates.iterator(); childIterator.hasNext(); playerStreetIndex += childTreeSize) {
               GameState childState = (GameState)childIterator.next();
               childTreeSize = buildGameTree(childState, streetActionCounts, terminalScenarios); // Recursive call processes child completely

               // Now childState.nodeId should be set to the actual child node ID
               int childNodeId = childState.nodeId;

               // After child is processed, get its winnable players
               int childWinnablePlayers = UnsafeMemoryStorage.getWinnablePlayers(childNodeId);

               // Add child's winnable players to current node
               winnablePlayersMask |= childWinnablePlayers;

               childIndex++;
            }

            // Store the computed winnable players mask
            UnsafeMemoryStorage.setWinnablePlayers(currentNodeIdSnapshot, winnablePlayersMask);

//            System.out.println("Branch " + currentNodeId + ": with bets " + Arrays.toString(state.bets) + " " + formatWinnablePlayers(winnablePlayersMask));
            // payoff node
            UnsafeMemoryStorage.setNextNodeId((long)savedNodeId, savedNodeId + playerStreetIndex);
            return playerStreetIndex + 1;
         }
      }
   }

   public static String formatWinnablePlayers(int bitmask) {
      if (bitmask == 0) {
         return "0 = 000₂ = No players";
      }

      StringBuilder binary = new StringBuilder();
      StringBuilder players = new StringBuilder();

      // Build binary representation (reverse order for readability)
      int remainingMask = bitmask;
      int bitCount = 0;
      while (remainingMask > 0 || bitCount < 3) { // At least 3 bits for clarity
         binary.insert(0, (remainingMask & 1));
         remainingMask >>= 1;
         bitCount++;
         if (bitCount >= 8) break; // Reasonable limit
      }

      // Build player list
      boolean first = true;
      for (int i = 0; i < 8; i++) { // Check up to 8 players
         if ((bitmask & (1 << i)) != 0) {
            if (!first) players.append(", ");
            players.append("Player ").append(i);
            first = false;
         }
      }

      if (players.length() == 0) {
         players.append("No players");
      }

      return String.format("%d = %s₂ = %s", bitmask, binary.toString(), players.toString());
   }
   static final boolean markAncestorsOfNode(int targetNodeId, int currentNodeId) {
      if (currentNodeId == targetNodeId) {
         UnsafeMemoryStorage.adjustedFlags[currentNodeId] = true;
         return true;
      } else {
         int actionCount = nodeActionCounts[currentNodeId];
         int savedNodeId = currentNodeId;

         for(int actionIdx = 0; actionIdx < actionCount; ++actionIdx) {
            ++currentNodeId;
            if (markAncestorsOfNode(targetNodeId, currentNodeId)) {
               UnsafeMemoryStorage.adjustedFlags[savedNodeId] = true;
               return true;
            }

            currentNodeId = UnsafeMemoryStorage.getNextNodeId((long)currentNodeId);
         }

         return false;
      }
   }

   public static void calculateCfrTableMemory() {
      cfrTableMemoryBytes = 0L;
      if (cfrTables != null) {
         for(int streetPlayerIndex = 0; streetPlayerIndex < cfrTables.length; ++streetPlayerIndex) {
            if (cfrTables[streetPlayerIndex] != null) {
               for(int bucketIndex = 0; bucketIndex < cfrTables[streetPlayerIndex].length; ++bucketIndex) {
                  if (cfrTables[streetPlayerIndex][bucketIndex] != null) {
                     cfrTableMemoryBytes += 8L * (long)cfrTables[streetPlayerIndex][bucketIndex].length;
                  }
               }
            }
         }
      }

   }

   private static boolean hasNonFoldAction(GameState gameState, int player) {
      if (gameState.firstPlayerToAct != player) {
         return false; // Not this player's turn
      }

      int[] actions = getAvailableActions(gameState); // Get available actions
      for (int action : actions) {
         if (!isFoldAction(action)) {
            return true;
         }
      }
      return false;
   }

   private static boolean isFoldAction(int action) {
      // nodeType == 0 means fold action
      return action == 0;
   }

   private static boolean isAllinAction(int action) {
      // nodeType == 0 means fold action
      return action == 3;
   }

   private static boolean isAllInPlayer(GameState gameState) {
      // Check if player has already committed all their chips

      int[] actions = getAvailableActions(gameState); // Get available actions
      for (int action : actions) {
         if (isAllinAction(action)) {
            return true;
         }
      }
      return false;
   }

   private static void countNodes(GameState state) {
      ++currentNodeId;
      if (state.gameStage >= 4) {
         ++terminalNodeCounter;
      } else {
         Iterator childIterator = getChildStates(state).iterator();

         while(childIterator.hasNext()) {
            countNodes((GameState)childIterator.next());
         }

      }
   }

   public static void clearAdjustments(int nodeId, int handIndex) {
      double[] adjustments;
      if ((adjustments = UnsafeMemoryStorage.getAdjustments(nodeId)) != null) {
         int tableIndex = UnsafeMemoryStorage.getPlayerStreetIndex((long)nodeId);
         double[] cfrTable = cfrTables[tableIndex][handIndex];
         int numActions = nodeActionCounts[nodeId];
         int offset = UnsafeMemoryStorage.getNodeOffset((long)nodeId);
         synchronized(cfrTable) {
            boolean hadAdjustments = false;

            int i;
            for(i = 0; i < nodeActionCounts[nodeId]; ++i) {
               if (adjustments[handIndex * numActions + i] >= 0.0D) {
                  hadAdjustments = true;
                  adjustments[handIndex * numActions + i] = -1.0D;
               }
            }

            if (hadAdjustments) {
               for(i = 0; i < numActions; ++i) {
                  if (avg[nodeId] != null) {
                     avg[nodeId][handIndex * numActions + i] = 0.0D;
                  }

                  cfrTable[offset + i] = 0.0D;
               }

               if (hasEV[tableIndex]) {
                  cfrTable[offset + numActions] = 0.0D;
                  cfrTable[offset + numActions + 1] = 0.0D;
               }
            }

         }
      }
   }

   public static double getAdjustment(int nodeId, int handIndex, int actionIndex) {
      if (UnsafeMemoryStorage.getAdjustments(nodeId) == null) {
         return -1.0D;
      } else {
         int numActions = nodeActionCounts[nodeId];
         return UnsafeMemoryStorage.getAdjustments(nodeId)[handIndex * numActions + actionIndex];
      }
   }

   public static void applyHandRangeAdjustments(int nodeId, HandRange handRange, card[] boardCards, int actionIndex, boolean skipZeroWeight) {
      if (actionIndex < 0) {
         skipZeroWeight = true;
      }

      int holeCardCount;
      card[] combinedCards = solver.card.createCardArray((holeCardCount = AnalysisPanel.getHoleCardCount()) + boardCards.length);

      int i;
      for(i = 0; i < boardCards.length; ++i) {
         combinedCards[holeCardCount + i] = boardCards[i];
      }

      for(i = 0; i < handRange.weights.length; ++i) {
         double weight = handRange.weights[i];
         if (!skipZeroWeight || weight > 1.0E-7D) {
            handRange.decodeComboIndex(i, combinedCards);
            int bucketIndex;
            if ((bucketIndex = computeBucketForHand(UnsafeMemoryStorage.getPlayerStreetIndex((long)nodeId) / 4, combinedCards, UnsafeMemoryStorage.getPlayerStreetIndex((long)nodeId) % 4)) >= 0) {
               if (actionIndex >= 0) {
                  setAdjustment(nodeId, bucketIndex, actionIndex, handRange.weights[i]);
               } else {
                  clearAdjustments(nodeId, bucketIndex);
               }
            }
         }
      }

   }

   public static void setAdjustment(int nodeId, int handIndex, int actionIndex, double adjustmentValue) {
      double[] cfrTable = cfrTables[UnsafeMemoryStorage.getPlayerStreetIndex((long)nodeId)][handIndex];
      int numActions = nodeActionCounts[nodeId];
      int tableOffset = UnsafeMemoryStorage.getNodeOffset((long)nodeId);
      int playerStreetIndex = UnsafeMemoryStorage.getPlayerStreetIndex((long)nodeId);
      double[] adjustments = UnsafeMemoryStorage.getOrCreateAdjustments(nodeId, buckets[playerStreetIndex] * nodeActionCounts[nodeId]);
      double[] avgStrategy = avg[nodeId];
      handIndex *= numActions;
      synchronized(cfrTable) {
         adjustments[handIndex + actionIndex] = adjustmentValue;
         double totalAdjustment = 0.0D;
         boolean hasUnsetAdjustments = false;

         int i;
         for(i = 0; i < numActions; ++i) {
            cfrTable[tableOffset + i] = 0.0D;
            if (avgStrategy != null) {
               avgStrategy[handIndex + i] = 0.0D;
            }

            if (adjustments[handIndex + i] >= 0.0D) {
               totalAdjustment += adjustments[handIndex + i];
            } else {
               hasUnsetAdjustments = true;
            }
         }

         if (hasEV[playerStreetIndex]) {
            cfrTable[tableOffset + numActions] = 0.0D;
            cfrTable[tableOffset + numActions + 1] = 0.0D;
         }

         if (totalAdjustment > 1.0D || !hasUnsetAdjustments) {
            for(i = 0; i < numActions; ++i) {
               if (adjustments[handIndex + i] > 0.0D) {
                  adjustments[handIndex + i] /= totalAdjustment;
               }
            }
         }

      }
   }

   public static void setPotPlusAdjustment(GameState state, int actionIndex, double adjustment) {
      if (adjustment != 0.0D) {
         double totalPotWithAdjustment = state.getTotalPot() + adjustment;
         int nodeId = (Integer)gameStateToNodeId.get(state);

         UnsafeMemoryStorage.getOrCreateBonus(nodeId, nodeActionCounts[nodeId])[actionIndex] = totalPotWithAdjustment;
      }
   }

   public static void applyAdjustment(GameState state, int actionIndex, double penalty) {

         int nodeId = (Integer)gameStateToNodeId.get(state);

         UnsafeMemoryStorage.getOrCreateBonus(nodeId, nodeActionCounts[nodeId])[actionIndex] = penalty;

   }

   public static GameState findGameStateByBranchID(int branchID) {
      // Search through the gameStateToNodeId map to find the GameState object with this branchID
      for (Object key : gameStateToNodeId.keySet()) {
         Object value = gameStateToNodeId.get(key);
         if (value instanceof Integer && ((Integer)value).intValue() == branchID) {
            if (key instanceof GameState) {
               return (GameState)key;
            }
         }
      }
      return null; // Not found
   }

   public static void addAdjustment(GameState state, int actionIndex, double adjustmentValue) {
      if (adjustmentValue != 0.0D) {
         double totalPot = state.getTotalPot();
         double scaledValue;
         if (icm == null) {
            scaledValue = totalPot * adjustmentValue;
         } else {
            double icmMultiplier;
            if (icm == null) {
               icmMultiplier = 1.0D;
            } else {
               double totalStacks = 0.0D;
               double totalIcm = 0.0D;

               for(int i = 0; i < gameState.stacks.length; ++i) {
                  totalStacks += (double)gameState.stacks[i];
               }

               for(int i = 0; i < icm.length; ++i) {
                  totalIcm += icm[i];
               }

               icmMultiplier = totalIcm * 1000.0D / totalStacks;
            }

            scaledValue = totalPot * icmMultiplier * adjustmentValue;
         }

         int nodeId;
         nodeId = (Integer)gameStateToNodeId.get(state);
         double[] adjustments = UnsafeMemoryStorage.getOrCreateBonus(nodeId, nodeActionCounts[nodeId]);
         adjustments[actionIndex] += scaledValue;
      }
   }

   public static void removeAdjustment(GameState state, int actionIndex) {
      int nodeId;

      if (UnsafeMemoryStorage.getBonus(nodeId = (Integer)gameStateToNodeId.get(state)) != null) {
         UnsafeMemoryStorage.getBonus(nodeId)[actionIndex] = 0.0D;
      }
   }

   public static boolean hasAdjustments(int nodeId, int handIndex) {
      if (UnsafeMemoryStorage.getAdjustments(nodeId) == null) {
         return false;
      } else {
         double[] adjustments = UnsafeMemoryStorage.getAdjustments(nodeId);
         handIndex *= nodeActionCounts[nodeId];

         for(int i = 0; i < nodeActionCounts[nodeId]; ++i) {
            if (adjustments[handIndex + i] >= 0.0D) {
               return true;
            }
         }

         return false;
      }
   }

   public static int[] computeBucketsForHand(card[] holeCards, card[] boardCards) {
      return computeBucketsForHandAndBoard(holeCards, boardCards, new int[4], new int[4]);
   }

   private static int[] computeStreetBucketsFromTexture(card[] combinedCards, int[] textureBuckets) {
      int[] streetBuckets;
      int[] result = streetBuckets = new int[4];
      int bucketIndex = preflopHandBucketMap.get(CardCombinations.computePreflopHandHash(combinedCards));
      streetBuckets[0] = bucketIndex;
      if (combinedCards.length > 2) {
         bucketIndex = (textureBuckets[0] * flopBuckets << 2) + streetBucketMaps[1].get(CardCombinations.computeFlopHandHash(combinedCards, result));
         streetBuckets[1] = bucketIndex;
         if (combinedCards.length > 5) {
            bucketIndex = (textureBuckets[1] * turnBuckets << 2) + streetBucketMaps[2].get(CardCombinations.computeTurnHandHash(combinedCards, result));
            streetBuckets[2] = bucketIndex;
            if (combinedCards.length > 6) {
               bucketIndex = textureBuckets[2] * riverBuckets + streetBucketMaps[3].get(CardCombinations.computeRiverHandHash(combinedCards, result));
               streetBuckets[3] = bucketIndex;
            }
         }
      }

      return streetBuckets;
   }

   private static int[] computeBucketsByOmahaHash(collections.LongIntHashMap bucketMap, card[] holeCards, card[] boardCards, int[] suitBuffer, int[] result) {
      long combinedKey = CardCombinations.normalizeAndHashHand(holeCards, boardCards, suitBuffer);
      result[3] = bucketMap.get(combinedKey);
      combinedKey /= 100L;
      result[2] = bucketMap.get(combinedKey);
      combinedKey /= 100L;
      result[1] = bucketMap.get(combinedKey);
      return result;
   }

   private static int[] computeBucketsForHandAndBoard(card[] holeCards, card[] boardCards, int[] suitBuffer, int[] result) {
      result[0] = preflopHandBucketMap.get(CardCombinations.computeHandHashByLength(holeCards, boardCards, suitBuffer, 2));
      if (holeCards.length > 2) {
         card[] combinedBoard;
         int flopTextureValue = CardCombinations.getCardArrayNumValue(combinedBoard = CardCombinations.writeOptimizedSuits((card[])Arrays.copyOfRange(holeCards, 2, holeCards.length), boardCards), 3);
         int turnTextureValue = CardCombinations.getCardArrayNumValue(combinedBoard, 4);
         int riverTextureValue = CardCombinations.getCardArrayNumValue(combinedBoard, 5);
         long flopKey = CardCombinations.computeHandHashByLength(holeCards, boardCards, suitBuffer, 5);
         result[1] = (flopTexture.get(flopTextureValue) * flopBuckets << 2) + streetBucketMaps[1].get(flopKey);
         if (holeCards.length > 5) {
            long turnKey = CardCombinations.computeHandHashByLength(holeCards, boardCards, suitBuffer, 6);
            result[2] = (turnTexture.get(turnTextureValue) * turnBuckets << 2) + streetBucketMaps[2].get(turnKey);
            if (holeCards.length > 6) {
               long riverKey = CardCombinations.computeHandHashByLength(holeCards, boardCards, suitBuffer, 7);
               result[3] = riverTexture.get(riverTextureValue) * riverBuckets + streetBucketMaps[3].get(riverKey);
            }
         }
      }

      return result;
   }

   private static int[] computeOmahaBucketsFromInts(int[] result, int[] textureBuckets, int[] cardValues, int[] sortedValues, int[] suitCounts, int handIndex, int totalCards) {
      // Use 5-card bucket calculation if gameType == 4
      if (AnalysisPanel.is5Card()) {
         return computeOmahaBucketsFromInts5c(result, textureBuckets, cardValues, sortedValues, suitCounts, handIndex, totalCards);
      }

      result[0] = preflopHandBucketMap.get(OmahaHandNormalizer.hashPreflop(cardValues, sortedValues, suitCounts));
      if (totalCards > 4 && textureBuckets != null) {
         result[1] = (textureBuckets[0] * flopBuckets << 2) + streetBucketMaps[1].get(OmahaHandNormalizer.hashFlop(cardValues, sortedValues, suitCounts));
         if (totalCards > 7) {
            result[2] = (textureBuckets[1] * turnBuckets << 2) + streetBucketMaps[2].get(OmahaHandNormalizer.hashTurn(cardValues, sortedValues, suitCounts));
            if (totalCards > 8) {
               int riverIndex = riverIndexMap.get(OmahaHandNormalizer.hashBoardByCount(cardValues, sortedValues, suitCounts, 9));
               result[3] = textureBuckets[2] * riverBuckets + riverBucketLookup[riverIndex].get(handIndex);
            }
         }
      }

      return result;
   }

   // 5-card version of bucket calculation
   private static int[] computeOmahaBucketsFromInts5c(int[] result, int[] textureBuckets, int[] cardValues, int[] sortedValues, int[] suitCounts, int handIndex, int totalCards) {
      result[0] = preflopHandBucketMap.get(OmahaHandNormalizer.hashPreflop5c(cardValues, sortedValues, suitCounts));
      if (totalCards > 5 && textureBuckets != null) {
         result[1] = (textureBuckets[0] * flopBuckets << 2) + streetBucketMaps[1].get(OmahaHandNormalizer.hashFlop5c(cardValues, sortedValues, suitCounts));
         if (totalCards > 8) {
            result[2] = (textureBuckets[1] * turnBuckets << 2) + streetBucketMaps[2].get(OmahaHandNormalizer.hashTurn5c(cardValues, sortedValues, suitCounts));
            if (totalCards > 9) {
               int riverIndex = riverIndexMap.get(OmahaHandNormalizer.hashBoardByCount5c(cardValues, sortedValues, suitCounts, 10));
               result[3] = textureBuckets[2] * riverBuckets + riverBucketLookup[riverIndex].get(handIndex);
            }
         }
      }

      return result;
   }

   public static int computeBucketForHand(int playerIndex, card[] cards, int street) {
      synchronized(tempCardArray) {
         if (AnalysisPanel.isHoldem()) {
            if (gameState.gameStage == 0) {
               return computeBucketsForHandAndBoard(cards, tempCardArray, suitNormBuffer, new int[4])[street];
            } else {
               long combinedKey = CardCombinations.normalizeAndHashHand(cards, tempCardArray, suitNormBuffer);
               int streetOffset;
               if ((streetOffset = street - gameState.gameStage) == 0) {
                  return postflopBucketMaps[playerIndex].get(combinedKey);
               } else {
                  return streetOffset == 1 ? postflopBucketMaps[playerIndex].get(combinedKey / 100L) : postflopBucketMaps[playerIndex].get(combinedKey / 100L / 100L);
               }
            }
         } else if (AnalysisPanel.is5Card()) {
            // 5-card Omaha logic
            for(int i = 0; i < cards.length; ++i) {
               cardValueBuffer[i] = cards[i].getFullDeckIndex52();
            }

            if (gameState.gameStage == 0) {
               int[] suitCounts = suitNormBuffer;
               int[] sortedValues = suitIndexBuffer;
               int[] cardValues = cardValueBuffer;
               int[] textureBuckets;
               if (cards.length > 5) {
                  textureBuckets = new int[3];
                  card[] boardOnly = CardCombinations.optimizeSuits((card[])Arrays.copyOfRange(cards, 5, cards.length));
                  textureBuckets[0] = flopTexture.get(CardCombinations.getCardArrayNumValue(boardOnly, 3));
                  textureBuckets[1] = cards.length > 8 ? turnTexture.get(CardCombinations.getCardArrayNumValue(boardOnly, 4)) : 0;
                  textureBuckets[2] = cards.length > 9 ? riverTexture.get(CardCombinations.getCardArrayNumValue(boardOnly, 5)) : 0;
               } else {
                  textureBuckets = null;
               }

               int bucketResult;
               if ((playerIndex = street) == 0) {
                  bucketResult = preflopHandBucketMap.get(OmahaHandNormalizer.hashPreflop5c(cardValues, sortedValues, suitCounts));
               } else if (playerIndex == 1) {
                  bucketResult = (textureBuckets[0] * flopBuckets << 2) + streetBucketMaps[1].get(OmahaHandNormalizer.hashFlop5c(cardValues, sortedValues, suitCounts));
               } else if (playerIndex == 2) {
                  bucketResult = (textureBuckets[1] * turnBuckets << 2) + streetBucketMaps[2].get(OmahaHandNormalizer.hashTurn5c(cardValues, sortedValues, suitCounts));
               } else {
                  playerIndex = riverIndexMap.get(OmahaHandNormalizer.hashBoardByCount5c(cardValues, sortedValues, suitCounts, 10));
                  // TODO: 5-card hand evaluator needed - using 4-card evaluator as placeholder
                  int handStrength = handeval.PloHandEvaluator.evaluatePlo5Hi(cardValues);
                  bucketResult = textureBuckets[2] * riverBuckets + riverBucketLookup[playerIndex].get(handStrength);
               }

               return bucketResult;
            } else {
               return postflopBucketMaps[playerIndex].get(OmahaHandNormalizer.normalizeMulti5c(cardValueBuffer, suitIndexBuffer, cards.length, suitNormBuffer, isoLevel));
            }
         } else {
            // 4-card Omaha logic
            for(int i = 0; i < cards.length; ++i) {
               cardValueBuffer[i] = cards[i].getFullDeckIndex52();
            }

            if (gameState.gameStage == 0) {
               int[] suitCounts = suitNormBuffer;
               int[] sortedValues = suitIndexBuffer;
               int[] cardValues = cardValueBuffer;
               int[] textureBuckets;
               if (cards.length > 4) {
                  textureBuckets = new int[3];
                  card[] boardOnly = CardCombinations.optimizeSuits((card[])Arrays.copyOfRange(cards, 4, cards.length));
                  textureBuckets[0] = flopTexture.get(CardCombinations.getCardArrayNumValue(boardOnly, 3));
                  textureBuckets[1] = cards.length > 7 ? turnTexture.get(CardCombinations.getCardArrayNumValue(boardOnly, 4)) : 0;
                  textureBuckets[2] = cards.length > 8 ? riverTexture.get(CardCombinations.getCardArrayNumValue(boardOnly, 5)) : 0;
               } else {
                  textureBuckets = null;
               }

               int bucketResult;
               if ((playerIndex = street) == 0) {
                  bucketResult = preflopHandBucketMap.get(OmahaHandNormalizer.hashPreflop(cardValues, sortedValues, suitCounts));
               } else if (playerIndex == 1) {
                  bucketResult = (textureBuckets[0] * flopBuckets << 2) + streetBucketMaps[1].get(OmahaHandNormalizer.hashFlop(cardValues, sortedValues, suitCounts));
               } else if (playerIndex == 2) {
                  bucketResult = (textureBuckets[1] * turnBuckets << 2) + streetBucketMaps[2].get(OmahaHandNormalizer.hashTurn(cardValues, sortedValues, suitCounts));
               } else {
                  playerIndex = riverIndexMap.get(OmahaHandNormalizer.hashBoardByCount(cardValues, sortedValues, suitCounts, 9));
                  int handStrength = AnalysisPanel.is5Card() ? handeval.PloHandEvaluator.evaluatePlo5Hi(cardValues) : handeval.PloHandEvaluator.evaluatePlo4Hi(cardValues);
                  if (AnalysisPanel.gameType == 2) {
                     handStrength |= handeval.PloHandEvaluator.evaluateLowHand(cardValues, sortedValues) << 16;
                  }

                  bucketResult = textureBuckets[2] * riverBuckets + riverBucketLookup[playerIndex].get(handStrength);
               }

               return bucketResult;
            } else {
               return postflopBucketMaps[playerIndex].get(OmahaHandNormalizer.normalizeMulti(cardValueBuffer, suitIndexBuffer, cards.length, suitNormBuffer, isoLevel));
            }
         }
      }
   }

   private static void init() throws InterruptedException, Throwable {
      initializeHasEVArray();
      if (!AnalysisPanel.isHoldem()) {
         initializeOmahaBuckets();
      } else {
         currentNodeId = 0;
         int[] textureCounts = new int[5];
         preflopHandBucketMap = new collections.LongIntHashMap();

         Iterator combosIterator;

         //combosIterator = solver.CardArrays.h().iterator();
         if (AnalysisPanel.gameType == 0) {
            combosIterator = solver.CardArrays.getHoldemStartingHandCombos().iterator();
         } else {
            combosIterator = solver.CardArrays.hShortdeck().iterator();
         }

         while(combosIterator.hasNext()) {
            CardCombinations combo = (CardCombinations)combosIterator.next();
            preflopHandBucketMap.put(combo.computeHash(), preflopHandBucketMap.size());
         }

         TLongIntHashMap flopBucketMap = null;
         TLongIntHashMap turnBucketMap = null;
         TLongIntHashMap riverBucketMap = null;

         /*collections.IntIntHashMap turnTextureShortDeck = null;
         collections.IntIntHashMap turnTextureMap;

         collections.IntIntHashMap riverTextureShortDeck = null;
         collections.IntIntHashMap riverTextureMap;*/

         AnalysisPanel.isDebugMode();
         String prefix = "";
         String sd = ""; if (AnalysisPanel.gameType == 3) { sd = "SD"; }
         if (maxSolveStreet > 1) {
            flopBucketMap = (TLongIntHashMap)solver.Equity.readObject(prefix + "mmnestedflop"+sd+"." + flopBuckets + ".4");
            if (solver.SolverRunner.stopRequested) {
               throw new InterruptedException();
            }

            if (flopBucketMap == null) {
               AnalysisPanel.isDebugMode();
               solver.Equity.generateFlopBuckets(flopBuckets, 4);
               flopBucketMap = (TLongIntHashMap)solver.Equity.readObject(prefix + "mmnestedflop"+sd+"." + flopBuckets + ".4");
            }

            Iterator flopIterator;
            if (AnalysisPanel.gameType == 0) {
               flopTexture = new collections.IntIntHashMap(1755);
               List fullDeckFlops = solver.CardArrays.generateFullDeckFlops();
               flopIterator = fullDeckFlops.iterator();
            } else {
               flopTexture = new collections.IntIntHashMap(573);
               List shortDeckFlops = solver.CardArrays.generateShortDeckFlops();
               flopIterator = shortDeckFlops.iterator();
            }

            //flopTexture = new collections.IntIntHashMap(573);
            //List shortDeckFlops = solver.CardArrays.generateShortDeckFlops();

            while(flopIterator.hasNext()) {
               card[] flopCards = (card[])flopIterator.next();
               flopTexture.put(CardCombinations.getCardArrayValue(flopCards), flopTexture.size());
            }
         }

         if (solver.SolverRunner.stopRequested) {
            throw new InterruptedException();
         } else {
            if (maxSolveStreet > 2) {
               turnBucketMap = (TLongIntHashMap)solver.Equity.readObject(prefix + "mmnestedturn"+sd+"." + turnBuckets + ".4");
               if (solver.SolverRunner.stopRequested) {
                  throw new InterruptedException();
               }

               if (turnBucketMap == null) {
                  solver.Equity.generateTurnBuckets(turnBuckets, 4);
                  turnBucketMap = (TLongIntHashMap)solver.Equity.readObject(prefix + "mmnestedturn"+sd+"." + turnBuckets + ".4");
               }

               AnalysisPanel.isDebugMode();
               turnTexture = TextureAbstractionLookup.generateTurnTexture(turnTextureType);
            }

            if (maxSolveStreet > 3) {
               riverBucketMap = (TLongIntHashMap)solver.Equity.readObject(prefix + "mmnestedriver"+sd+"." + riverBuckets);
               if (solver.SolverRunner.stopRequested) {
                  throw new InterruptedException();
               }

               if (riverBucketMap == null) {
                  solver.Equity.generateRiverBuckets(riverBuckets);
                  riverBucketMap = (TLongIntHashMap)solver.Equity.readObject(prefix + "mmnestedriver"+sd+"." + riverBuckets);
               }

               AnalysisPanel.isDebugMode();
               riverTexture = TextureAbstractionLookup.generateRiverTexture(riverTextureType);
            }

            (streetBucketMaps = new collections.LongIntHashMap[4])[3] = solver.Equity.convertToLongIntHashMap((gnu.trove.map.TLongIntMap)riverBucketMap);
            streetBucketMaps[2] = solver.Equity.convertToLongIntHashMap((gnu.trove.map.TLongIntMap)turnBucketMap);
            streetBucketMaps[1] = solver.Equity.convertToLongIntHashMap((gnu.trove.map.TLongIntMap)flopBucketMap);
            currentNodeId = 0;
            assignNodeIdsAndCountActions(new GameState(gameState), textureCounts);
            currentNodeId = 0;
            int flopTextureCount = flopTexture == null ? 0 : flopTexture.maxValue() + 1;
            int turnTextureCount = turnTexture == null ? 0 : turnTexture.maxValue() + 1;
            int riverTextureCount = riverTexture == null ? 0 : riverTexture.maxValue() + 1;
            System.lineSeparator();
            buckets = new int[gameState.nWay << 2];

            for(int playerIdx = 0; playerIdx < gameState.nWay; ++playerIdx) {
               buckets[playerIdx << 2] = preflopHandBucketMap.size();
               buckets[(playerIdx << 2) + 1] = flopTextureCount * flopBuckets << 2;
               if (flopBuckets == 0) {
                  buckets[(playerIdx << 2) + 1] = 881374;
               }

               buckets[(playerIdx << 2) + 2] = turnTextureCount * turnBuckets << 2;
               buckets[(playerIdx << 2) + 3] = riverTextureCount * riverBuckets;
            }

            EquityTableCache.clearEquityTables();
            System.lineSeparator();
            buildGameTree();
            currentNodeId = 0;
         }
      }
   }

   private static void initializePostflop() throws InterruptedException {
      initializeHasEVArray();
      byte numBoardCards;
      int i;
      if (AnalysisPanel.isHoldem()) {
         if (gameState.gameStage == 1) {
            numBoardCards = 3;
         } else if (gameState.gameStage == 2) {
            numBoardCards = 4;
         } else {
            numBoardCards = 5;
         }

         boardCardsArray = new card[numBoardCards];

         for(i = 0; i < numBoardCards; ++i) {
            boardCardsArray[i] = solver.card.parseCard((String)solver.MainTabbedPane.enteredBoard.get(i));
         }

         playerHands = new card[gameState.nWay][][];
         playerCardBytes = new byte[gameState.nWay][];
         playerEquities = new double[gameState.nWay][];
         playerStrategyWeights = new double[gameState.nWay][];

         for(i = 0; i < gameState.nWay; ++i) {
            HandRange playerRange = AnalysisPanel.getRangeForPlayer(i);
            HandRange filteredRange;
            (filteredRange = new HandRange(playerRange)).removeKnownCards(boardCardsArray);
            AnalysisPanel.isDebugMode();
            playerHands[i] = filteredRange.getAllCards();
            playerEquities[i] = filteredRange.getNonZeroWeights();
            playerStrategyWeights[i] = filteredRange.weights;
            playerCardBytes[i] = new byte[2 * playerEquities[i].length];

            for(int handIndex = 0; handIndex < playerHands[i].length; ++handIndex) {
               playerCardBytes[i][handIndex << 1] = (byte)playerHands[i][handIndex][0].getFullDeckIndex();
               playerCardBytes[i][(handIndex << 1) + 1] = (byte)playerHands[i][handIndex][1].getFullDeckIndex();
            }
         }

         currentNodeId = 0;
         postflopBucketMaps = new collections.LongIntHashMap[gameState.nWay];
         buckets = new int[gameState.nWay << 2];

         for(i = 0; i < gameState.nWay; ++i) {
            postflopBucketMaps[i] = new collections.LongIntHashMap();
            int[] bucketCounts = computePostflopBucketsHoldem(playerHands[i], gameState.gameStage, boardCardsArray, postflopBucketMaps[i]);
            buckets[(i << 2) + 1] = bucketCounts[0];
            buckets[(i << 2) + 2] = bucketCounts[1];
            buckets[(i << 2) + 3] = bucketCounts[2];
         }

         assignNodeIdsAndCountActions(gameState, new int[5]);
         currentNodeId = 0;
         buildGameTree();
         currentNodeId = 0;
      } else {
         handeval.PloHandEvaluator.initialize();
         if (gameState.gameStage == 1) {
            numBoardCards = 3;
         } else if (gameState.gameStage == 2) {
            numBoardCards = 4;
         } else {
            numBoardCards = 5;
         }

         boardCardsArray = new card[numBoardCards];

         for(i = 0; i < numBoardCards; ++i) {
            boardCardsArray[i] = solver.card.parseCard((String)solver.MainTabbedPane.enteredBoard.get(i));
         }

         playerCardBytes = new byte[gameState.nWay][];
         playerEquities = new double[gameState.nWay][];
         playerStrategyWeights = new double[gameState.nWay][];
         int[][] handWeights = new int[gameState.nWay][];

         for(int playerIdx = 0; playerIdx < gameState.nWay; ++playerIdx) {
            OmahaHandRange playerRange = (OmahaHandRange)AnalysisPanel.getRangeForPlayer(playerIdx);
            (playerRange = new OmahaHandRange(playerRange)).removeKnownCards(boardCardsArray);
            playerStrategyWeights[playerIdx] = playerRange.weights;
            playerCardBytes[playerIdx] = playerRange.toByteArray();
            playerEquities[playerIdx] = playerRange.getNonZeroWeights();
            handWeights[playerIdx] = playerRange.getNonZeroIndices();
         }

         currentNodeId = 0;
         Test.preBucketing();
         if (!skipBucketing) {
            postflopBucketMaps = new collections.LongIntHashMap[gameState.nWay];
            buckets = new int[gameState.nWay << 2];
            ArrayList bucketingThreads = new ArrayList();

            for(int playerIdx = 0; playerIdx < gameState.nWay; ++playerIdx) {
               Thread bucketThread;
               (bucketThread = new Thread(new CFRBucketingTask(playerIdx, handWeights))).start();
               bucketingThreads.add(bucketThread);
            }

            Iterator threadIterator = bucketingThreads.iterator();

            while(threadIterator.hasNext()) {
               Thread thread = (Thread)threadIterator.next();

               while(thread.isAlive()) {
                  Thread.sleep(20L);
               }
            }
         }

         Test.postBucketing();
         System.lineSeparator();
         if (solver.SolverRunner.stopRequested) {
            throw new InterruptedException();
         } else {
            assignNodeIdsAndCountActions(gameState, new int[5]);
            currentNodeId = 0;
            buildGameTree();
            currentNodeId = 0;
            currentNodeId = 0;
         }
      }
   }

   private static void initializeOmahaBuckets() throws InterruptedException, Throwable {
      currentNodeId = 0;
      // 5-card has more unique hands than 4-card
      int numHands = AnalysisPanel.is5Card() ? 134459 : 16432;  // 5-card ~134k unique hands vs 4-card ~16k
      preflopHandBucketMap = new collections.LongIntHashMap(numHands);
      Iterator handIterator = AnalysisPanel.is5Card() ? OmahaHandNormalizer.enumeratePreflop5c().iterator() : OmahaHandNormalizer.enumeratePreflop().iterator();

      while(handIterator.hasNext()) {
         long normalizedHand = (Long)handIterator.next();
         preflopHandBucketMap.put(normalizedHand, preflopHandBucketMap.size());
      }

      handeval.PloHandEvaluator.initialize();
      int[] textureCounts = new int[5];
      assignNodeIdsAndCountActions(new GameState(gameState), textureCounts);
      currentNodeId = 0;
      gnu.trove.map.TLongIntMap flopBucketMap = null;
      gnu.trove.map.TLongIntMap turnBucketMap = null;
      streetBucketMaps = new collections.LongIntHashMap[4];
      // Use separate folders for 5-card PLO (Omaha5) vs 4-card PLO (Omaha/Omaha8)
      String folderPath;
      if (AnalysisPanel.is5Card()) {
         folderPath = "Omaha5" + File.separator;
      } else {
         folderPath = AnalysisPanel.gameType == 2 ? "Omaha8" + File.separator : "Omaha" + File.separator;
      }
      // Use separate file prefixes: omaha5 for 5-card, omaha for 4-card
      String filePrefix = AnalysisPanel.is5Card() ? "omaha5" : "omaha";
      // River index file: PLO5 uses "Omaha5/omaha5riverindex", PLO4 uses "omahariverindex" (in root)
      String riverIndexFile = AnalysisPanel.is5Card() ? folderPath + "omaha5riverindex" : "omahariverindex";

      if (maxSolveStreet > 2) {
         turnBucketMap = (gnu.trove.map.TLongIntMap)solver.Equity.readObject(folderPath + filePrefix + "mmnestedturn." + turnBuckets + ".4");
         if (solver.SolverRunner.stopRequested) {
            throw new InterruptedException();
         }

         if (turnBucketMap == null) {
            solver.MainTabbedPane.setStatusWithProgress("Generating turn buckets.", 0, 1);
            OmahaEquityGenerator.generateTurnBuckets(turnBuckets, 4, AnalysisPanel.gameType == 2);
            turnBucketMap = (gnu.trove.map.TLongIntMap)solver.Equity.readObject(folderPath + filePrefix + "mmnestedturn." + turnBuckets + ".4");
         }

         turnTexture = TextureAbstractionLookup.generateTurnTexture(turnTextureType);
      }

      if (solver.SolverRunner.stopRequested) {
         throw new InterruptedException();
      } else {
         if (maxSolveStreet > 3) {
            riverIndexMap = solver.Equity.convertToLongIntHashMap((gnu.trove.map.TLongIntMap)((TLongIntHashMap)solver.Equity.readObject(riverIndexFile)));
            if (version >= 10097L) {
               riverBucketLookup = (TIntIntHashMap[])solver.Equity.readObject(folderPath + "nriver." + riverBuckets);
               if (solver.SolverRunner.stopRequested) {
                  throw new InterruptedException();
               }

               if (riverBucketLookup == null) {
                  solver.MainTabbedPane.setStatusWithProgress("Generating river buckets.", 0, 1);
                  OmahaEquityGenerator.generateRiverBuckets(riverBuckets, AnalysisPanel.gameType == 2);
                  riverBucketLookup = (TIntIntHashMap[])solver.Equity.readObject(folderPath + "nriver." + riverBuckets);
               }
            } else {
               riverBucketLookup = (TIntIntHashMap[])solver.Equity.readObject(folderPath + filePrefix + "mmnestedriver." + riverBuckets);
               if (solver.SolverRunner.stopRequested) {
                  throw new InterruptedException();
               }

               if (riverBucketLookup == null) {
                  solver.MainTabbedPane.setStatus("Generating river buckets.");
                  OmahaEquityGenerator.generateRiverBucketsLegacy(riverBuckets, AnalysisPanel.gameType == 2);
                  riverBucketLookup = (TIntIntHashMap[])solver.Equity.readObject(folderPath + filePrefix + "mmnestedriver." + riverBuckets);
               }
            }

            riverTexture = TextureAbstractionLookup.generateRiverTexture(riverTextureType);
         }

         EquityTableCache.clearEquityTables();
         if (maxSolveStreet > 1) {
            flopBucketMap = (gnu.trove.map.TLongIntMap)solver.Equity.readObject(folderPath + filePrefix + "mmnestedflop." + flopBuckets + ".4");
            if (solver.SolverRunner.stopRequested) {
               throw new InterruptedException();
            }

            if (flopBucketMap == null) {
               solver.MainTabbedPane.setStatusWithProgress("Generating flop buckets.", 0, 1);
               OmahaEquityGenerator.generateFlopBuckets(flopBuckets, 4, AnalysisPanel.gameType == 2);
               flopBucketMap = (gnu.trove.map.TLongIntMap)solver.Equity.readObject(folderPath + filePrefix + "mmnestedflop." + flopBuckets + ".4");
            }

            System.lineSeparator();
         }

         if (solver.SolverRunner.stopRequested) {
            throw new InterruptedException();
         } else {
            streetBucketMaps[1] = solver.Equity.convertToLongIntHashMap(flopBucketMap);
            flopBucketMap = null;
            System.gc();
            streetBucketMaps[2] = solver.Equity.convertToLongIntHashMap(turnBucketMap);
            turnBucketMap = null;
            System.gc();
            currentNodeId = 0;
            flopTexture = new collections.IntIntHashMap(1755);
            Iterator flopIterator = solver.CardArrays.generateFullDeckFlops().iterator();

            while(flopIterator.hasNext()) {
               card[] flopCards = (card[])flopIterator.next();
               flopTexture.put(CardCombinations.getCardArrayValue(flopCards), flopTexture.size());
            }

            int turnTextureCount = turnTexture == null ? 0 : turnTexture.maxValue() + 1;
            int riverTextureCount = riverTexture == null ? 0 : riverTexture.maxValue() + 1;
            buckets = new int[gameState.nWay << 2];

            // 5-card has more unique hole card combinations
            int numPreflopBuckets = AnalysisPanel.is5Card() ? 134459 : 16432;
            // 5-card has more possible flop+hand combinations
            int flopBucketMultiplier = AnalysisPanel.is5Card() ? 498409379 : 79791556;

            for(int playerIdx = 0; playerIdx < gameState.nWay; ++playerIdx) {
               buckets[playerIdx << 2] = numPreflopBuckets;
               buckets[(playerIdx << 2) + 1] = 1755 * flopBuckets << 2;
               if (flopBuckets == 0) {
                  buckets[(playerIdx << 2) + 1] = flopBucketMultiplier;
               }

               buckets[(playerIdx << 2) + 2] = turnTextureCount * turnBuckets << 2;
               buckets[(playerIdx << 2) + 3] = riverTextureCount * riverBuckets;
            }

            System.lineSeparator();
            buildGameTree();
            currentNodeId = 0;
         }
      }
   }

   public static final boolean shouldUseChipEV() {
      boolean result;
      if (AnalysisPanel.gameType == 2 || icm == null && AnalysisPanel.getRootGameState().nWay > 3 && AnalysisPanel.allStacksEqual()) {
         result = true;
      } else {
         result = false;
      }

      return result;
   }

   public static void initializeSolver(long versionNumber) throws InterruptedException, Throwable {
      solver.SolverRunner.isStopped = false;
      solver.SolverRunner.stopRequested = false;
      if (AnalysisPanel.gameType == 2 && !AnalysisPanel.allStacksEqual()) {
         JOptionPane.showMessageDialog(solver.MainTabbedPane.instance, "Only symmetrical stacksizes can be used for Omaha Hi/Lo.");
         throw new InterruptedException();
      } else {
         if (!usePrecomputedEquity) {
            setSavedFile((File)null);
         }

         version = versionNumber;
         savedFile = null;
         gameState = new GameState(AnalysisPanel.getRootGameState());
         useChipEV = shouldUseChipEV();
         solver.MainTabbedPane.actionPath.clear();
         evs = new double[gameState.nWay];
         evIterationCounts = new long[gameState.nWay];
         handeval.tables.HandRankEvaluator.initialize();
         AnalysisPanel.isDebugMode();
         HandStatisticCollection.clearPool();
         nodeActionCache.clear();
         GameTree gameTree;
         (gameTree = AnalysisPanel.INSTANCE.gameTree).filteredNodes.clear();
         gameTree.lastFilteredRoot = null;
         gameTree.lastFilterSettings = null;
         gameTree.undoButton.setEnabled(false);
         gameTree.redoButton.setEnabled(false);
         if (!usePrecomputedEquity) {
            handRangeReader = null;
         }

         new CardArrays();
         if (GameSettings.d != null) {
            viewSettings = GameSettings.d;
            isoLevel = 1;
         } else {
            viewSettings = null;
            isoLevel = GameSettings.c;
         }

         iterations = 0L;
         adjustmentListener = null;
         cfrTables = null;
         avg = null;
         hasEV = null;
         System.gc();
         if (gameState.gameStage == 0) {
            init();
         } else {
            initializePostflop();
         }

         solveStartTime = System.currentTimeMillis();
         System.gc();
         solver.MainTabbedPane.updateStatusForSolverState();
      }
   }

   public static int[] getAvailableActions(GameState state) {
      int[] actions;
      if ((actions = (int[])nodeActionCache.get(state)) != null) {
         return actions;
      } else {
         actions = AnalysisPanel.getAnswersForState(state);
         state = ((PokerTreeNode)PokerTreeNode.nodeCache.get(state)).gameState;
         nodeActionCache.put(state, actions);
         return actions;
      }
   }

   private static List getChildStates(GameState parentState) {
      ArrayList childStates = new ArrayList();
      int[] actions;
      int actionsLen = (actions = getAvailableActions(parentState)).length;

      for(int i = 0; i < actionsLen; ++i) {
         int action = actions[i];
         childStates.add(getChildState(parentState, action));
      }

      return childStates;
   }

   public static GameState getChildState(GameState parentState, int actionType) {
      if (parentState == null) {
         return null;
      } else {
         PokerTreeNode parentNode;
         if ((parentNode = (PokerTreeNode)PokerTreeNode.nodeCache.get(parentState)) == null) {
            return null;
         } else {
            Enumeration childEnum = parentNode.children();

            while(childEnum.hasMoreElements()) {
               PokerTreeNode childNode;
               if ((childNode = (PokerTreeNode)childEnum.nextElement()).nodeType == actionType) {
                  return childNode.gameState;
               }
            }

            return null;
         }
      }
   }

   private static void assignNodeIdsAndCountActions(GameState state, int[] stageCounts) {
      ++currentNodeId;
      if (state.gameStage < 4) {
         ++stageCounts[state.gameStage];
         gameStateToNodeId.put(state, currentNodeId);
         Iterator childIter = getChildStates(state).iterator();

         while(childIter.hasNext()) {
            assignNodeIdsAndCountActions((GameState)childIter.next(), stageCounts);
         }

      }
   }

   public static final void executeIteration(CFRIterator iterator) {
      int[] cardDeckIdxs;
      int[] handStrengths;
      int[] resultMasksBuffer;
      int[] boardTextureValues;
      int iter;
      int[] playerOrderArr;
      int initIdx;
      int iterationIdx;
      int boardIdx;
      int playerActionBase;
      int playerIdx;
      int[] handStrengthsRef;
      int[] resultMasksRef;
      int[] scratchAlias;
      int playerOrderLen;
      int[] boardTextureScratch;
      card[] cardBufferA;
      card[] cardBufferB;
      int postflopScratch;
      int postflopScratchTmp;
      int[] boardTextures;
      int innerIdx;
      int cursor;
      if (AnalysisPanel.isHoldem()) {
         card[] sevenCardHand;
         card[] sortedSevenCardHand;
         int sevenCardInitIdx;
         if (gameState.gameStage == 0) {
            iterator = iterator;
            cardDeckIdxs = new int[7];
            sevenCardHand = new card[7];
            sortedSevenCardHand = new card[7];

            for(sevenCardInitIdx = 0; sevenCardInitIdx < 7; ++sevenCardInitIdx) {
               sortedSevenCardHand[sevenCardInitIdx] = new card(0, 0);
            }

            resultMasksBuffer = new int[(handStrengths = new int[gameState.nWay]).length];
            boardTextureScratch = new int[4];
            int[][] unusedScratchPerPlayer = new int[gameState.nWay][4];
            cardBufferA = new card[5];
            int[] flopTextureBuffer = new int[3];
            playerOrderArr = new int[gameState.nWay];

            for(initIdx = 0; initIdx < playerOrderArr.length; playerOrderArr[initIdx] = initIdx++) {
            }

            CardArrays cardArrays = new CardArrays();
            AnalysisPanel.isDebugMode();

            for(iterationIdx = 0; iterationIdx < 150000 && !iterator.isPauseRequested() && !iterator.isStopRequested(); ++iterationIdx) {
               int[] unusedScratch5 = new int[5];
               int[] unusedScratch7 = new int[7];
               scratchAlias = boardTextureScratch;
               resultMasksRef = resultMasksBuffer;
               handStrengthsRef = handStrengths;
               card[] playerHoleCards = sevenCardHand;
               CardArrays cardArraysAlias = cardArrays;
               CFRIterator cfr = iterator;
               cardArrays.partialShuffle((gameState.nWay << 1) + 5, iterator.getThreadRandom());
               byte deckOffset = 0;
               int deckCursor = deckOffset + 1;
               cardBufferA[0] = cardArrays.deck[0];
               ++deckCursor;
               cardBufferA[1] = cardArrays.deck[1];
               ++deckCursor;
               cardBufferA[2] = cardArrays.deck[2];
               ++deckCursor;
               cardBufferA[3] = cardArrays.deck[3];
               ++deckCursor;
               cardBufferA[4] = cardArrays.deck[4];
               sevenCardHand[2] = cardBufferA[0];
               sevenCardHand[3] = cardBufferA[1];
               sevenCardHand[4] = cardBufferA[2];
               sevenCardHand[5] = cardBufferA[3];
               sevenCardHand[6] = cardBufferA[4];
               cardDeckIdxs[2] = cardBufferA[0].getFullDeckIndex();
               cardDeckIdxs[3] = cardBufferA[1].getFullDeckIndex();
               cardDeckIdxs[4] = cardBufferA[2].getFullDeckIndex();
               cardDeckIdxs[5] = cardBufferA[3].getFullDeckIndex();
               cardDeckIdxs[6] = cardBufferA[4].getFullDeckIndex();
               CardCombinations.writeOptimizedSuits(cardBufferA, sortedSevenCardHand);
               boardTextures = CardCombinations.getBoardTexturesValues(sortedSevenCardHand, flopTextureBuffer);

               int preflopPlayerIdx;
               int preflopPlayerIdxTmp = 0;
               for(preflopPlayerIdx = 0; preflopPlayerIdx < gameState.nWay; ++preflopPlayerIdx) {
                  playerHoleCards[0] = cardArraysAlias.deck[deckCursor++];
                  playerHoleCards[1] = cardArraysAlias.deck[deckCursor++];
                  playerActionBase = preflopPlayerIdx << 2;
                  cfr.setCfrTable(playerActionBase, preflopHandBucketMap.get(CardCombinations.computePreflopHandHash(playerHoleCards)));
                  long flopBucketKey = CardCombinations.computeFlopHandHash(playerHoleCards, scratchAlias);
                  long turnBucketKey = CardCombinations.computeTurnHandHash(playerHoleCards, scratchAlias);
                  long riverBucketKey = CardCombinations.computeRiverHandHash(playerHoleCards, scratchAlias);

                  final int playerActionBaseFinal = playerActionBase;
                  final int[] boardTexturesFinal = boardTextures;
                  CFRLoaderInterface cfrLoader = (loaderIter) -> {
                     loaderIter.setCfrTable(playerActionBaseFinal + 1, (boardTexturesFinal[0] * flopBuckets << 2) + streetBucketMaps[1].get(flopBucketKey));
                     loaderIter.setCfrTable(playerActionBaseFinal + 2, (boardTexturesFinal[1] * turnBuckets << 2) + streetBucketMaps[2].get(turnBucketKey));
                     loaderIter.setCfrTable(playerActionBaseFinal + 3, boardTexturesFinal[2] * riverBuckets + streetBucketMaps[3].get(riverBucketKey));
                  };
                  cfr.setCfrLoader(playerActionBase + 1, cfrLoader);
                  cfr.setCfrLoader(playerActionBase + 2, cfrLoader);
                  cfr.setCfrLoader(playerActionBase + 3, cfrLoader);
               }

               deckCursor -= gameState.nWay << 1;
               AnalysisPanel.isDebugMode();
               preflopPlayerIdx = handeval.tables.HandRankEvaluator.precomputeBoardKey(cardDeckIdxs);

               boolean acesOnBoard = false;
               if (AnalysisPanel.gameType == 3) {
                  for (int i = 2; i<7; i++) {
                     if (cardDeckIdxs[i] > 47) {
                        acesOnBoard = true;
                        break;
                     }
                  }

                  if (acesOnBoard) {
                     preflopPlayerIdxTmp = handeval.tables.HandRankEvaluator.precomputeBoardKeyA5swap(cardDeckIdxs);
                  }
               }

               int playerLoopIdx;
               int handStrength;
               int tmp2;
               for(playerLoopIdx = 0; playerLoopIdx < gameState.nWay; ++playerLoopIdx) {
                  playerHoleCards[0] = cardArraysAlias.deck[deckCursor++];
                  playerHoleCards[1] = cardArraysAlias.deck[deckCursor++];

                  int holeCard0Idx = playerHoleCards[0].getFullDeckIndex();
                  int holeCard1Idx = playerHoleCards[1].getFullDeckIndex();

                  handStrength = handeval.tables.HandRankEvaluator.evaluateWithHoleCards(preflopPlayerIdx, holeCard0Idx, holeCard1Idx);

                  if (AnalysisPanel.gameType == 3) {
                     if (acesOnBoard) {
                        if ( (holeCard0Idx > 47) || (holeCard1Idx > 47)) {
                           tmp2 = handeval.tables.HandRankEvaluator.evaluateWithHoleCardsA5swap(preflopPlayerIdxTmp, holeCard0Idx, holeCard1Idx);
                        } else {
                           tmp2 = handeval.tables.HandRankEvaluator.evaluateWithHoleCards(preflopPlayerIdxTmp, holeCard0Idx, holeCard1Idx);
                        }
                     } else {
                        if ( (holeCard0Idx > 47) || (holeCard1Idx > 47)) {
                           tmp2 = handeval.tables.HandRankEvaluator.evaluateWithHoleCardsA5swap(preflopPlayerIdx, holeCard0Idx, holeCard1Idx);
                        } else {
                           tmp2 = handeval.tables.HandRankEvaluator.evaluateWithHoleCards(preflopPlayerIdx, holeCard0Idx, holeCard1Idx);
                        }
                     }

                     handStrength = Math.max(handStrength, tmp2);

                     if ( ((handStrength >= 5863) && (handStrength < 7140)) || (handStrength >= 7296) ) {
                        handStrength += 2000;
                     }
                  }

                  handStrengthsRef[playerLoopIdx] = handStrength;
               }

               cfr.flushEVAccumulator();
               int[] playerOrderRef;
               //playerOrderLen = player count
               playerOrderLen = (playerOrderRef = playerOrderArr).length;
               cfr.setHandStrengths(handStrengthsRef);

               for(cursor = 0; cursor < playerOrderLen; ++cursor) {
                  playerLoopIdx = playerOrderRef[cursor];  // Current player

                  if (useChipEV) {
                     int[] payoutIdxs = cfr.computeHandResultMasks(handStrengthsRef, resultMasksRef, playerLoopIdx);
                     cfr.setResultMasks(payoutIdxs);
                  } else {
                     cfr.setEquityValues(adjustmentListener.getPayoff(preflopPlayerIdx, handStrengthsRef, playerLoopIdx));
                  }
                  cfr.runIterationForPlayer(playerLoopIdx);
               }

               if (cfr.getThreadRandom().nextDouble() < 1.0D / (2.0D * (double)(gameState.nWay * gameState.nWay))) {
                  for(playerLoopIdx = 0; playerLoopIdx < gameState.nWay; ++playerLoopIdx) {
                     cfr.accumulateAvgStrategyForPlayer(playerLoopIdx);
                  }
               }
            }

         } else {
            AnalysisPanel.isDebugMode();
            cardDeckIdxs = new int[7];
            sevenCardHand = new card[7];
            sortedSevenCardHand = new card[7];

            for(sevenCardInitIdx = 0; sevenCardInitIdx < 7; ++sevenCardInitIdx) {
               sortedSevenCardHand[sevenCardInitIdx] = new card(0, 0);
            }

            resultMasksBuffer = new int[(handStrengths = new int[gameState.nWay]).length];
            boardTextureValues = new int[4];
            boardTextureScratch = new int[4];
            SplittableRandom rng = iterator.getThreadRandom();
            cardBufferA = new card[gameState.nWay];
            cardBufferB = new card[gameState.nWay];
            playerOrderArr = new int[gameState.nWay];
            long boardBitmask = 0L;

            for(boardIdx = 0; boardIdx < boardCardsArray.length; ++boardIdx) {
               sevenCardHand[boardIdx + 2] = boardCardsArray[boardIdx];
               cardDeckIdxs[boardIdx + 2] = boardCardsArray[boardIdx].getFullDeckIndex();
               boardBitmask = solver.Equity.setBitLong(boardBitmask, cardDeckIdxs[boardIdx + 2]);
            }

            int[] playerOrderHoldem = new int[gameState.nWay];

            for(iter = 0; iter < playerOrderHoldem.length; playerOrderHoldem[iter] = iter++) {
            }

            for(iter = 0; iter < 145000 && !iterator.isPauseRequested() && !iterator.isStopRequested(); ++iter) {
               long dealtBitmask = sampleHoldemHoleCards(boardBitmask, cardBufferA, cardBufferB, playerOrderArr, rng);
               if (AnalysisPanel.gameType == 0) {
                  if (gameState.gameStage == 1) {
                     sevenCardHand[5] = solver.CardArrays.pickRandomCardExcluding(dealtBitmask, rng);
                     cardDeckIdxs[5] = sevenCardHand[5].getFullDeckIndex();
                     dealtBitmask = solver.Equity.setBitLong(dealtBitmask, cardDeckIdxs[5]);
                     sevenCardHand[6] = solver.CardArrays.pickRandomCardExcluding(dealtBitmask, rng);
                     cardDeckIdxs[6] = sevenCardHand[6].getFullDeckIndex();
                  } else if (gameState.gameStage == 2) {
                     sevenCardHand[6] = solver.CardArrays.pickRandomCardExcluding(dealtBitmask, rng);
                     cardDeckIdxs[6] = sevenCardHand[6].getFullDeckIndex();
                  }
               } else {
                  if (gameState.gameStage == 1) {
                     sevenCardHand[5] = solver.CardArrays.aShortdeck(dealtBitmask, rng);
                     cardDeckIdxs[5] = sevenCardHand[5].getFullDeckIndex();
                     dealtBitmask = solver.Equity.setBitLong(dealtBitmask, cardDeckIdxs[5]);
                     sevenCardHand[6] = solver.CardArrays.aShortdeck(dealtBitmask, rng);
                     cardDeckIdxs[6] = sevenCardHand[6].getFullDeckIndex();
                  } else if (gameState.gameStage == 2) {
                     sevenCardHand[6] = solver.CardArrays.aShortdeck(dealtBitmask, rng);
                     cardDeckIdxs[6] = sevenCardHand[6].getFullDeckIndex();
                  }
               }

               postflopScratch = handeval.tables.HandRankEvaluator.precomputeBoardKey(cardDeckIdxs);
               postflopScratchTmp = 0;

               boolean acesOnBoard = false;
               if (AnalysisPanel.gameType == 3) {
                  for (int i = 2; i<7; i++) {
                     if (cardDeckIdxs[i] > 47) {
                        acesOnBoard = true;
                        break;
                     }
                  }

                  if (acesOnBoard) {
                     postflopScratchTmp = handeval.tables.HandRankEvaluator.precomputeBoardKeyA5swap(cardDeckIdxs);
                  }
               }

               int handStrength;
               int tmp2;
               for(playerIdx = 0; playerIdx < gameState.nWay; ++playerIdx) {
                  sevenCardHand[0] = cardBufferA[playerIdx];
                  sevenCardHand[1] = cardBufferB[playerIdx];

                  int holeCard0Idx = sevenCardHand[0].getFullDeckIndex();
                  int holeCard1Idx = sevenCardHand[1].getFullDeckIndex();

                  int[] bucketsByStreet = computeBucketsByOmahaHash(postflopBucketMaps[playerIdx], sevenCardHand, sortedSevenCardHand, boardTextureScratch, boardTextureValues);

                  for(innerIdx = 1; innerIdx < bucketsByStreet.length; ++innerIdx) {
                     if (bucketsByStreet[innerIdx] >= 0) {
                        iterator.setCfrTable((playerIdx << 2) + innerIdx, bucketsByStreet[innerIdx]);
                     }
                  }

                  handStrength = handeval.tables.HandRankEvaluator.evaluateWithHoleCards(postflopScratch, holeCard0Idx, holeCard1Idx);

                  if (AnalysisPanel.gameType == 3) {
                     if (acesOnBoard) {
                        if ( (holeCard0Idx > 47) || (holeCard1Idx > 47)) {
                           tmp2 = handeval.tables.HandRankEvaluator.evaluateWithHoleCardsA5swap(postflopScratchTmp, holeCard0Idx, holeCard1Idx);
                        } else {
                           tmp2 = handeval.tables.HandRankEvaluator.evaluateWithHoleCards(postflopScratchTmp, holeCard0Idx, holeCard1Idx);
                        }
                     } else {
                        if ( (holeCard0Idx > 47) || (holeCard1Idx > 47)) {
                           tmp2 = handeval.tables.HandRankEvaluator.evaluateWithHoleCardsA5swap(postflopScratch, holeCard0Idx, holeCard1Idx);
                        } else {
                           tmp2 = handeval.tables.HandRankEvaluator.evaluateWithHoleCards(postflopScratch, holeCard0Idx, holeCard1Idx);
                        }
                     }

                     handStrength = Math.max(handStrength, tmp2);

                     if ( ((handStrength >= 5863) && (handStrength < 7140)) || (handStrength >= 7296) ) {
                        handStrength += 2000;
                     }
                  }

                  handStrengths[playerIdx] = handStrength;
               }

               iterator.flushEVAccumulator();
               scratchAlias = playerOrderHoldem;
               int playerOrderLenLocal = playerOrderHoldem.length;
               iterator.setHandStrengths(handStrengths);
               for(int playerOrderIdx = 0; playerOrderIdx < playerOrderLenLocal; ++playerOrderIdx) {
                  innerIdx = scratchAlias[playerOrderIdx];
                  if (useChipEV) {
                     int[] payoutIdxs = iterator.computeHandResultMasks(handStrengths, resultMasksBuffer, innerIdx);
                     iterator.setResultMasks(payoutIdxs);
                  } else {
                     iterator.setEquityValues(adjustmentListener.getPayoff(playerIdx, handStrengths, innerIdx));
                  }
                  iterator.runIterationForPlayer(innerIdx);
               }

               if (iterator.getThreadRandom().nextInt(4 * gameState.nWay * gameState.nWay) == 1) {
                  for(innerIdx = 0; innerIdx < gameState.nWay; ++innerIdx) {
                     iterator.accumulateAvgStrategyForPlayer(innerIdx);
                  }
               }
            }

         }
      } else {
         int[] playerHandStrengthsOmaha;
         int[] playerLowStrengthsOmaha;
         int[] lowCardScratch;
         int[] playerOrderOmaha;
         if (gameState.gameStage == 0) {
            iterator = iterator;
            int holeCards0 = AnalysisPanel.is5Card() ? 5 : 4;
            cardDeckIdxs = new int[holeCards0 + 5];  // 5-card: 10, 4-card: 9
            playerLowStrengthsOmaha = new int[(playerHandStrengthsOmaha = new int[gameState.nWay]).length];
            handStrengths = new int[playerHandStrengthsOmaha.length];
            SplittableRandom rng = iterator.getThreadRandom();
            boardTextureScratch = new int[holeCards0 + 5];  // 5-card: 10, 4-card: 9
            lowCardScratch = new int[13];
            cardBufferA = new card[5];

            for(int boardInitIdx = 0; boardInitIdx < 5; ++boardInitIdx) {
               cardBufferA[boardInitIdx] = new card(0, 0);
            }

            cardBufferB = new card[5];
            playerOrderArr = new int[4];
            playerOrderOmaha = new int[4];
            int[] playerOrderPreflop = new int[gameState.nWay];

            for(boardIdx = 0; boardIdx < playerOrderPreflop.length; playerOrderPreflop[boardIdx] = boardIdx++) {
            }

            CardArrays cardArraysOmaha = new CardArrays();

            for(iter = 0; iter < 145000 && !iterator.isPauseRequested() && !iterator.isStopRequested(); ++iter) {
               int[] cardValueScratchAlias = playerOrderOmaha;
               resultMasksBuffer = boardTextureScratch;
               int[] playerActionScratchAlias = handStrengths;
               scratchAlias = lowCardScratch;
               resultMasksRef = playerLowStrengthsOmaha;
               handStrengthsRef = playerHandStrengthsOmaha;
               int[] boardTextureValuesFinal = playerOrderArr;
               boardTextures = cardDeckIdxs;
               CardArrays cardArraysAlias = cardArraysOmaha;
               CFRIterator cfr = iterator;
               // 5-card: (gameState.nWay * 5) + 5, 4-card: (gameState.nWay * 4) + 5
               cardArraysOmaha.partialShuffle(AnalysisPanel.is5Card() ? (gameState.nWay * 5) + 5 : (gameState.nWay << 2) + 5, rng);
               byte cursorBase = 0;
               cursor = cursorBase + 1;
               cardBufferB[0] = cardArraysOmaha.deck[0];
               ++cursor;
               cardBufferB[1] = cardArraysOmaha.deck[1];
               ++cursor;
               cardBufferB[2] = cardArraysOmaha.deck[2];
               ++cursor;
               cardBufferB[3] = cardArraysOmaha.deck[3];
               ++cursor;
               cardBufferB[4] = cardArraysOmaha.deck[4];
               // Board cards: 5-card uses indices 5-9, 4-card uses indices 4-8
               cardDeckIdxs[holeCards0] = cardBufferB[0].getFullDeckIndex52();
               cardDeckIdxs[holeCards0 + 1] = cardBufferB[1].getFullDeckIndex52();
               cardDeckIdxs[holeCards0 + 2] = cardBufferB[2].getFullDeckIndex52();
               cardDeckIdxs[holeCards0 + 3] = cardBufferB[3].getFullDeckIndex52();
               cardDeckIdxs[holeCards0 + 4] = cardBufferB[4].getFullDeckIndex52();
               playerOrderLen = AnalysisPanel.is5Card() ? handeval.PloHandEvaluator.detectBoardFlushSuit5c(cardDeckIdxs) : handeval.PloHandEvaluator.detectBoardFlushSuit(cardDeckIdxs);
               CardCombinations.writeOptimizedSuits(cardBufferB, cardBufferA);
               CardCombinations.getBoardTexturesValues(cardBufferA, playerOrderArr);
               // 5-card: c5c with 10 cards, 4-card: c with 9 cards
               final long riverIndexKey = AnalysisPanel.is5Card() ? OmahaHandNormalizer.hashBoardByCount5c(cardDeckIdxs, boardTextureScratch, playerOrderOmaha, 10) : OmahaHandNormalizer.hashBoardByCount(cardDeckIdxs, boardTextureScratch, playerOrderOmaha, 9);

               int playerHandValue;
               int playerLowValue;
               for(innerIdx = 0; innerIdx < gameState.nWay; ++innerIdx) {
                  boardTextures[0] = cardArraysAlias.deck[cursor++].getFullDeckIndex52();
                  boardTextures[1] = cardArraysAlias.deck[cursor++].getFullDeckIndex52();
                  boardTextures[2] = cardArraysAlias.deck[cursor++].getFullDeckIndex52();
                  boardTextures[3] = cardArraysAlias.deck[cursor++].getFullDeckIndex52();
                  if (AnalysisPanel.is5Card()) {
                     boardTextures[4] = cardArraysAlias.deck[cursor++].getFullDeckIndex52();
                  }
                  playerHandValue = AnalysisPanel.is5Card() ? handeval.PloHandEvaluator.evaluatePlo5HiWithSuit(boardTextures, playerOrderLen) : handeval.PloHandEvaluator.evaluatePlo4HiWithSuit(boardTextures, playerOrderLen);
                  handStrengthsRef[innerIdx] = -playerHandValue;
                  if (AnalysisPanel.gameType == 2) {
                     playerLowValue = handeval.PloHandEvaluator.evaluateLowHand(boardTextures, scratchAlias);
                     resultMasksRef[innerIdx] = playerLowValue;
                     playerHandValue |= playerLowValue << 16;
                  }

                  int playerActionBaseLocal = innerIdx << 2;
                  // 5-card: use d5c, c5c, b5c; 4-card: use d, c, b
                  cfr.setCfrTable(playerActionBaseLocal, preflopHandBucketMap.get(AnalysisPanel.is5Card() ? OmahaHandNormalizer.hashPreflop5c(boardTextures, resultMasksBuffer, cardValueScratchAlias) : OmahaHandNormalizer.hashPreflop(boardTextures, resultMasksBuffer, cardValueScratchAlias)));
                  long turnBucketKey = AnalysisPanel.is5Card() ? OmahaHandNormalizer.hashFlop5c(boardTextures, resultMasksBuffer, cardValueScratchAlias) : OmahaHandNormalizer.hashFlop(boardTextures, resultMasksBuffer, cardValueScratchAlias);
                  long flopBucketKey = AnalysisPanel.is5Card() ? OmahaHandNormalizer.hashTurn5c(boardTextures, resultMasksBuffer, cardValueScratchAlias) : OmahaHandNormalizer.hashTurn(boardTextures, resultMasksBuffer, cardValueScratchAlias);

                  int playerHandValueFinal = playerHandValue;
                  CFRLoaderInterface cfrLoader = (loaderIter) -> {
                     loaderIter.setCfrTable(playerActionBaseLocal + 1, (boardTextureValuesFinal[0] * flopBuckets << 2) + streetBucketMaps[1].get(turnBucketKey));
                     loaderIter.setCfrTable(playerActionBaseLocal + 2, (boardTextureValuesFinal[1] * turnBuckets << 2) + streetBucketMaps[2].get(flopBucketKey));
                     loaderIter.setCfrTable(playerActionBaseLocal + 3, boardTextureValuesFinal[2] * riverBuckets + riverBucketLookup[riverIndexMap.get(riverIndexKey)].get(playerHandValueFinal));
                  };
                  cfr.setCfrLoader(playerActionBaseLocal + 1, cfrLoader);
                  cfr.setCfrLoader(playerActionBaseLocal + 2, cfrLoader);
                  cfr.setCfrLoader(playerActionBaseLocal + 3, cfrLoader);
               }

               innerIdx = adjustmentListener.lookupRankingIndex(handStrengthsRef, handStrengths);
               cfr.flushEVAccumulator();
               int[] playerOrderPreflopRef;
               playerActionBase = (playerOrderPreflopRef = playerOrderPreflop).length;
               cfr.setHandStrengths(handStrengthsRef);

               for(playerLowValue = 0; playerLowValue < playerActionBase; ++playerLowValue) {
                  playerHandValue = playerOrderPreflopRef[playerLowValue];
                  if (useChipEV) {
                     int[] payoutIdxs = cfr.computeHandResultMasks(handStrengthsRef, playerActionScratchAlias, playerHandValue);  // CHANGED
                     cfr.setResultMasks(payoutIdxs);  // CHANGED
                     if (AnalysisPanel.gameType == 2) {
                        cfr.setSidePotMasks(buildWinTieMasks(resultMasksRef, playerActionScratchAlias, playerHandValue));
                     }
                  } else {
                     cfr.setEquityValues(adjustmentListener.getPayoff(innerIdx, handStrengthsRef, playerHandValue));
                  }

                  cfr.runIterationForPlayer(playerHandValue);
               }

               if (cfr.getThreadRandom().nextInt(2 * gameState.nWay * gameState.nWay) == 1) {
                  for(playerHandValue = 0; playerHandValue < gameState.nWay; ++playerHandValue) {
                     cfr.accumulateAvgStrategyForPlayer(playerHandValue);
                  }
               }
            }

         } else {
            iterator = iterator;
            int holeCards = AnalysisPanel.is5Card() ? 5 : 4;
            cardDeckIdxs = new int[holeCards + 5];  // hole cards + 5 board cards
            playerHandStrengthsOmaha = new int[holeCards + 5];
            playerLowStrengthsOmaha = new int[4];
            handStrengths = new int[gameState.nWay];
            resultMasksBuffer = null;
            boardTextureValues = new int[handStrengths.length];
            SplittableRandom rng = iterator.getThreadRandom();
            lowCardScratch = null;
            if (AnalysisPanel.gameType == 2) {
               lowCardScratch = new int[13];
               resultMasksBuffer = new int[handStrengths.length];
            }

            int[] dealtHoleCardIdxs = new int[gameState.nWay * holeCards];  // nWay * holeCards for indexing
            long boardBitmask = 0L;

            for(initIdx = 0; initIdx < boardCardsArray.length; ++initIdx) {
               cardDeckIdxs[initIdx + holeCards] = boardCardsArray[initIdx].getFullDeckIndex52();
               boardBitmask = solver.Equity.setBitLong(boardBitmask, cardDeckIdxs[initIdx + holeCards]);
            }

            playerOrderOmaha = new int[gameState.nWay];

            for(iterationIdx = 0; iterationIdx < playerOrderOmaha.length; playerOrderOmaha[iterationIdx] = iterationIdx++) {
            }

            long initialBoardBitmask = boardBitmask;

            for(iter = 0; iter < 155000 && !iterator.isPauseRequested() && !iterator.isStopRequested(); ++iter) {
               boardBitmask = AnalysisPanel.is5Card() ? sampleOmahaHoleCards5c(initialBoardBitmask, dealtHoleCardIdxs, rng) : sampleOmahaHoleCards(initialBoardBitmask, dealtHoleCardIdxs, rng);
               if (gameState.gameStage == 1) {
                  do {
                     cardDeckIdxs[holeCards + 3] = rng.nextInt(52);
                  } while(solver.Equity.isBitSetLong(boardBitmask, cardDeckIdxs[holeCards + 3]));

                  boardBitmask = solver.Equity.setBitLong(boardBitmask, cardDeckIdxs[holeCards + 3]);

                  do {
                     cardDeckIdxs[holeCards + 4] = rng.nextInt(52);
                  } while(solver.Equity.isBitSetLong(boardBitmask, cardDeckIdxs[holeCards + 4]));
               } else if (gameState.gameStage == 2) {
                  do {
                     cardDeckIdxs[holeCards + 4] = rng.nextInt(52);
                  } while(solver.Equity.isBitSetLong(boardBitmask, cardDeckIdxs[holeCards + 4]));
               }

               int boardEvalState = AnalysisPanel.is5Card() ? handeval.PloHandEvaluator.detectBoardFlushSuit5c(cardDeckIdxs) : handeval.PloHandEvaluator.detectBoardFlushSuit(cardDeckIdxs);

               int bucketIdx;
               for(playerActionBase = 0; playerActionBase < gameState.nWay; ++playerActionBase) {
                  cardDeckIdxs[0] = dealtHoleCardIdxs[playerActionBase * holeCards];
                  cardDeckIdxs[1] = dealtHoleCardIdxs[playerActionBase * holeCards + 1];
                  cardDeckIdxs[2] = dealtHoleCardIdxs[playerActionBase * holeCards + 2];
                  cardDeckIdxs[3] = dealtHoleCardIdxs[playerActionBase * holeCards + 3];
                  if (AnalysisPanel.is5Card()) {
                     cardDeckIdxs[4] = dealtHoleCardIdxs[playerActionBase * holeCards + 4];
                  }
                  if (gameState.gameStage < 3) {
                     // For 5-card: use 3-param a5c for turn bucket (matches reference)
                     // Turn bucket - 5-card uses a5c(cardDeckIdxs, playerHandStrengthsOmaha, playerLowStrengthsOmaha), 4-card uses a(cardDeckIdxs, playerHandStrengthsOmaha, playerLowStrengthsOmaha)
                     long turnBucketKey = AnalysisPanel.is5Card() ? OmahaHandNormalizer.hashTurnBoard5c(cardDeckIdxs, playerHandStrengthsOmaha, playerLowStrengthsOmaha) : OmahaHandNormalizer.hashTurnBoard(cardDeckIdxs, playerHandStrengthsOmaha, playerLowStrengthsOmaha);
                     bucketIdx = postflopBucketMaps[playerActionBase].get(turnBucketKey);
                     iterator.setCfrTable((playerActionBase << 2) + 2, bucketIdx);
                     if (gameState.gameStage < 2) {
                        // Flop bucket - divide by 100L (same as reference)
                        bucketIdx = postflopBucketMaps[playerActionBase].get(turnBucketKey / 100L);
                        iterator.setCfrTable((playerActionBase << 2) + 1, bucketIdx);
                     }
                  }

                  // River bucket - 5-card uses 4-param a5c, 4-card uses 4-param a
                  postflopScratch = postflopBucketMaps[playerActionBase].get(AnalysisPanel.is5Card() ? OmahaHandNormalizer.hashRiverWithLo5c(cardDeckIdxs, playerHandStrengthsOmaha, playerLowStrengthsOmaha, isoLevel) : OmahaHandNormalizer.hashRiverWithLo(cardDeckIdxs, playerHandStrengthsOmaha, playerLowStrengthsOmaha, isoLevel));
                  iterator.setCfrTable((playerActionBase << 2) + 3, postflopScratch);
                  System.lineSeparator();
                  handStrengths[playerActionBase] = AnalysisPanel.is5Card() ? -handeval.PloHandEvaluator.evaluatePlo5HiWithSuit(cardDeckIdxs, boardEvalState) : -handeval.PloHandEvaluator.evaluatePlo4HiWithSuit(cardDeckIdxs, boardEvalState);
                  if (AnalysisPanel.gameType == 2) {
                     resultMasksBuffer[playerActionBase] = handeval.PloHandEvaluator.evaluateLowHand(cardDeckIdxs, lowCardScratch);
                  }
               }

               playerActionBase = adjustmentListener.lookupRankingIndex(handStrengths, boardTextureValues);
               iterator.flushEVAccumulator();
               int[] playerOrderRef = playerOrderOmaha;
               bucketIdx = playerOrderOmaha.length;
               iterator.setHandStrengths(handStrengths);

               for(playerIdx = 0; playerIdx < bucketIdx; ++playerIdx) {
                  postflopScratch = playerOrderRef[playerIdx];
                  if (useChipEV) {
                     int[] payoutIdxs = iterator.computeHandResultMasks(handStrengths, boardTextureValues, postflopScratch);  // CHANGED
                     iterator.setResultMasks(payoutIdxs);  // CHANGED
                     if (AnalysisPanel.gameType == 2) {
                        iterator.setSidePotMasks(buildWinTieMasks(resultMasksBuffer, boardTextureValues, postflopScratch));
                     }
                  } else {
                     iterator.setEquityValues(adjustmentListener.getPayoff(playerActionBase, handStrengths, postflopScratch));
                  }

                  iterator.runIterationForPlayer(postflopScratch);
               }

               if (iterator.getThreadRandom().nextDouble() < 1.0D / (double)(2 * gameState.nWay * gameState.nWay)) {
                  for(postflopScratch = 0; postflopScratch < gameState.nWay; ++postflopScratch) {
                     iterator.accumulateAvgStrategyForPlayer(postflopScratch);
                  }
               }
            }

         }
      }

   }

   public static void main(String[] args) {
      solver.Equity.setBitLong(0L, 2);
      solver.Equity.setBitLong(0L, 21);
      solver.Equity.setBitLong(0L, 51);
      SplittableRandom rng = new SplittableRandom(23626227L);
      (new OmahaHandRange()).fillAllWeights();
      HandRange handRange = ResultsReader.a.parseRange("15%", new card[0], 1);
      playerCardBytes = new byte[5][];
      playerEquities = new double[5][];
      long[][] playerComboBitmasks = new long[5][];

      for(int playerIdx = 0; playerIdx < 5; ++playerIdx) {
         playerCardBytes[playerIdx] = handRange.toByteArray();
         playerEquities[playerIdx] = handRange.getNonZeroWeights();
         playerComboBitmasks[playerIdx] = new long[playerEquities[playerIdx].length];

         for(int comboIdx = 0; comboIdx < playerEquities[playerIdx].length; ++comboIdx) {
            int comboStride = comboIdx << 2;
            playerComboBitmasks[playerIdx][comboIdx] = solver.Equity.setBitLong(playerComboBitmasks[playerIdx][comboIdx], playerCardBytes[playerIdx][comboStride]);
            playerComboBitmasks[playerIdx][comboIdx] = solver.Equity.setBitLong(playerComboBitmasks[playerIdx][comboIdx], playerCardBytes[playerIdx][comboStride + 1]);
            playerComboBitmasks[playerIdx][comboIdx] = solver.Equity.setBitLong(playerComboBitmasks[playerIdx][comboIdx], playerCardBytes[playerIdx][comboStride + 2]);
            playerComboBitmasks[playerIdx][comboIdx] = solver.Equity.setBitLong(playerComboBitmasks[playerIdx][comboIdx], playerCardBytes[playerIdx][comboStride + 3]);
         }
      }

      int[] dealtCardIdxs = new int[20];

      int iter;
      for(iter = 0; iter < 100000; ++iter) {
         sampleOmahaHoleCards(0L, dealtCardIdxs, rng);
      }

      for(iter = 0; iter < 200000; ++iter) {
         sampleOmahaHoleCards(0L, dealtCardIdxs, rng);
      }

      System.lineSeparator();
      System.lineSeparator();
   }

   private static long sampleOmahaHoleCards(long boardBitmask, int[] outDealtIdxs, SplittableRandom rng) {
      retryDeal:
      while(true) {
         long bitmask = boardBitmask;

         int playerIdx;
         int comboIdx;
         for(playerIdx = 0; playerIdx < playerCardBytes.length; ++playerIdx) {
            double[] playerEquityRow = playerEquities[playerIdx];

            for(comboIdx = rng.nextInt(playerEquityRow.length); playerEquityRow[comboIdx] < 0.9999999999D && rng.nextDouble() > playerEquityRow[comboIdx]; comboIdx = rng.nextInt(playerEquityRow.length)) {
            }

            int comboStride = comboIdx << 2;
            byte[] playerCardRow = playerCardBytes[playerIdx];
            if (solver.Equity.isBitSetLong(bitmask, playerCardRow[comboStride]) || solver.Equity.isBitSetLong(bitmask, playerCardRow[comboStride + 1]) || solver.Equity.isBitSetLong(bitmask, playerCardRow[comboStride + 2]) || solver.Equity.isBitSetLong(bitmask, playerCardRow[comboStride + 3])) {
               continue retryDeal;
            }

            bitmask = solver.Equity.setBitLong(solver.Equity.setBitLong(solver.Equity.setBitLong(solver.Equity.setBitLong(bitmask, playerCardRow[comboStride]), playerCardRow[comboStride + 1]), playerCardRow[comboStride + 2]), playerCardRow[comboStride + 3]);
            outDealtIdxs[playerIdx << 2] = comboStride;
         }

         for(playerIdx = 0; playerIdx < playerCardBytes.length; ++playerIdx) {
            byte[] playerCardRow = playerCardBytes[playerIdx];
            comboIdx = outDealtIdxs[playerIdx << 2];
            outDealtIdxs[playerIdx << 2] = playerCardRow[comboIdx];
            outDealtIdxs[(playerIdx << 2) + 1] = playerCardRow[comboIdx + 1];
            outDealtIdxs[(playerIdx << 2) + 2] = playerCardRow[comboIdx + 2];
            outDealtIdxs[(playerIdx << 2) + 3] = playerCardRow[comboIdx + 3];
         }

         return bitmask;
      }
   }

   // 5-card version of card dealing method
   private static long sampleOmahaHoleCards5c(long boardBitmask, int[] outDealtIdxs, SplittableRandom rng) {
      retryDeal:
      while(true) {
         long bitmask = boardBitmask;

         int playerIdx;
         int comboIdx;
         for(playerIdx = 0; playerIdx < playerCardBytes.length; ++playerIdx) {
            double[] playerEquityRow = playerEquities[playerIdx];

            for(comboIdx = rng.nextInt(playerEquityRow.length); playerEquityRow[comboIdx] < 0.9999999999D && rng.nextDouble() > playerEquityRow[comboIdx]; comboIdx = rng.nextInt(playerEquityRow.length)) {
            }

            int comboStride = comboIdx * 5;
            byte[] playerCardRow = playerCardBytes[playerIdx];
            if (solver.Equity.isBitSetLong(bitmask, playerCardRow[comboStride]) || solver.Equity.isBitSetLong(bitmask, playerCardRow[comboStride + 1]) || solver.Equity.isBitSetLong(bitmask, playerCardRow[comboStride + 2]) || solver.Equity.isBitSetLong(bitmask, playerCardRow[comboStride + 3]) || solver.Equity.isBitSetLong(bitmask, playerCardRow[comboStride + 4])) {
               continue retryDeal;
            }

            bitmask = solver.Equity.setBitLong(solver.Equity.setBitLong(solver.Equity.setBitLong(solver.Equity.setBitLong(solver.Equity.setBitLong(bitmask, playerCardRow[comboStride]), playerCardRow[comboStride + 1]), playerCardRow[comboStride + 2]), playerCardRow[comboStride + 3]), playerCardRow[comboStride + 4]);
            outDealtIdxs[playerIdx * 5] = comboStride;
         }

         for(playerIdx = 0; playerIdx < playerCardBytes.length; ++playerIdx) {
            byte[] playerCardRow = playerCardBytes[playerIdx];
            comboIdx = outDealtIdxs[playerIdx * 5];
            outDealtIdxs[playerIdx * 5] = playerCardRow[comboIdx];
            outDealtIdxs[playerIdx * 5 + 1] = playerCardRow[comboIdx + 1];
            outDealtIdxs[playerIdx * 5 + 2] = playerCardRow[comboIdx + 2];
            outDealtIdxs[playerIdx * 5 + 3] = playerCardRow[comboIdx + 3];
            outDealtIdxs[playerIdx * 5 + 4] = playerCardRow[comboIdx + 4];
         }

         return bitmask;
      }
   }

   private static final long sampleHoldemHoleCards(long boardBitmask, card[] outHoleCardsA, card[] outHoleCardsB, int[] outComboIdxs, SplittableRandom rng) {
      retryDeal:
      while(true) {
         long bitmask = boardBitmask;

         int playerIdx;
         for(playerIdx = 0; playerIdx < gameState.nWay; ++playerIdx) {
            double[] playerEquityRow = playerEquities[playerIdx];

            int comboIdx;
            for(comboIdx = rng.nextInt(playerEquityRow.length); playerEquityRow[comboIdx] < 1.0D && rng.nextDouble() > playerEquityRow[comboIdx]; comboIdx = rng.nextInt(playerEquityRow.length)) {
            }

            byte[] playerCardRow = playerCardBytes[playerIdx];
            int comboStride = comboIdx << 1;
            if (solver.Equity.isBitSetLong(bitmask, playerCardRow[comboStride]) || solver.Equity.isBitSetLong(bitmask, playerCardRow[comboStride + 1])) {
               continue retryDeal;
            }

            bitmask = solver.Equity.setBitLong(solver.Equity.setBitLong(bitmask, playerCardRow[comboStride]), playerCardRow[comboStride + 1]);
            outComboIdxs[playerIdx] = comboIdx;
         }

         for(playerIdx = 0; playerIdx < gameState.nWay; ++playerIdx) {
            outHoleCardsA[playerIdx] = playerHands[playerIdx][outComboIdxs[playerIdx]][0];
            outHoleCardsB[playerIdx] = playerHands[playerIdx][outComboIdxs[playerIdx]][1];
         }

         return bitmask;
      }
   }

   private static int[] buildWinTieMasks(int[] handStrengths, int[] outMasks, int targetIdx) {
      outMasks[0] = 0;
      outMasks[1] = 0;

      for(int i = 0; i < handStrengths.length; ++i) {
         if (handStrengths[i] != 32767 && handStrengths[targetIdx] >= handStrengths[i]) {
            if (handStrengths[targetIdx] == handStrengths[i]) {
               outMasks[0] |= 1 << i;
            } else {
               outMasks[1] |= 1 << i;
            }
         }
      }

      return outMasks;
   }

   public static boolean hasOverlappingCards(card[] cards1, card[] cards2) {
      card[] firstCards = cards1;
      int firstLen = cards1.length;

      for(int i = 0; i < firstLen; ++i) {
         card cardA = firstCards[i];
         card[] secondCards = cards2;
         int secondLen = cards2.length;

         for(int j = 0; j < secondLen; ++j) {
            card cardB = secondCards[j];
            if (cardA.rank == cardB.rank && cardA.suit == cardB.suit) {
               return true;
            }
         }
      }

      return false;
   }

   private static double[] normalizeInPlace(double[] values) {
      double sum = 0.0D;

      int i;
      for(i = 0; i < values.length; ++i) {
         sum += values[i];
      }

      if (sum > 0.0D) {
         for(i = 0; i < values.length; ++i) {
            values[i] /= sum;
         }
      } else {
         for(i = 0; i < values.length; ++i) {
            values[i] = 1.0D / (double)values.length;
         }
      }

      return values;
   }

   private static int collectPlayerActionIndices(int[] outNodeIds, int[] outActionIndices, int[] actionPath, int playerSeat) {
      GameState state = gameState;
      int outputCursor = 0;
      int[] pathAlias = actionPath;
      int pathLen = actionPath.length;

      for(int pathIdx = 0; pathIdx < pathLen; ++pathIdx) {
         int pathAction = pathAlias[pathIdx];
         if (state.firstPlayerToAct != playerSeat) {
            state = getChildState(state, pathAction);
         } else {
            int matchedActionIdx = 0;
            int[] legalActions;
            int numLegal = (legalActions = getAvailableActions(state)).length;

            for(int legalIdx = 0; legalIdx < numLegal; ++legalIdx) {
               int legalAction = legalActions[legalIdx];
               if (pathAction == legalAction) {
                  break;
               }

               ++matchedActionIdx;
            }

            outNodeIds[outputCursor] = (Integer)gameStateToNodeId.get(state);
            outActionIndices[outputCursor] = matchedActionIdx;
            ++outputCursor;
            state = getChildState(state, pathAction);
         }
      }

      return outputCursor;
   }

   private static double computeReachProbabilityByPath(int[] actions, double initialReach, int targetPlayer, int[] nodeIdsByStage) {
      double reach = initialReach;
      GameState currentState = gameState;
      int[] actionsIter = actions;
      int actionsLen = actions.length;

      for(int actionIdx = 0; actionIdx < actionsLen; ++actionIdx) {
         int action = actionsIter[actionIdx];
         if (currentState.firstPlayerToAct == targetPlayer) {
            int matchIdx = 0;
            int[] availableActions;
            int actionCount = (availableActions = getAvailableActions(currentState)).length;

            for(int availIdx = 0; availIdx < actionCount; ++availIdx) {
               int candidateAction = availableActions[availIdx];
               if (action == candidateAction) {
                  break;
               }

               ++matchIdx;
            }

            actionCount = nodeIdsByStage[currentState.gameStage];
            double freq = getStrategyFrequency((Integer)gameStateToNodeId.get(currentState), actionCount, matchIdx);
            if ((reach *= freq) <= 1.0E-7D) {
               return 0.0D;
            }
         }

         currentState = getChildState(currentState, action);
      }

      return reach;
   }

   private static double computeReachProbabilityByNodes(int[] nodeIds, int[] actionIdxs, double initialReach, int[] actionCountsByBucket) {
      double reach = initialReach;

      for(int i = 0; i < nodeIds.length; ++i) {
         int nodeId;
         double freq = getStrategyFrequency(nodeId = nodeIds[i], actionCountsByBucket[UnsafeMemoryStorage.getPlayerStreetIndex((long)nodeId) % 4], actionIdxs[i]);
         if ((reach *= freq) <= 1.0E-7D) {
            return 0.0D;
         }
      }

      return reach;
   }

   public static HandRange[] buildActionWeightedRanges(GameState state, ArrayList playerStatsList, int action) {
      HandRange[] ranges;
      (ranges = new HandRange[3])[0] = new HandRange();
      ranges[1] = new HandRange();
      int actionSlot;
      int loopIdx;
      int loopLen;
      if (state == null) {
         ranges[1] = ranges[0];
         if (gameState.gameStage > 0) {
            actionSlot = 0;
            card[][] playerHandArray;
            loopLen = (playerHandArray = playerHands[0]).length;

            for(loopIdx = 0; loopIdx < loopLen; ++loopIdx) {
               card[] hand = playerHandArray[loopIdx];
               ranges[0].applyStringPattern(hand[0] + solver.HashUtil.decodeBy24(new char[0]) + hand[1], playerEquities[0][actionSlot]);
               ++actionSlot;
            }
         } else {
            for(actionSlot = 0; actionSlot < 1326; ++actionSlot) {
               HandRange target = ranges[0];
               double weight = 1.0D;
               target.weights[actionSlot] = weight;
            }
         }

         return ranges;
      } else {
         actionSlot = 0;
         int[] availableActions;
         loopLen = (availableActions = getAvailableActions(state)).length;

         for(loopIdx = 0; loopIdx < loopLen && availableActions[loopIdx] != action; ++loopIdx) {
            ++actionSlot;
         }

         Iterator statsIter = buildPlayerHandStatistics(state, playerStatsList).iterator();

         while(statsIter.hasNext()) {
            PlayerHandStatistic[] statsForHand;
            if ((statsForHand = (PlayerHandStatistic[])statsIter.next()) != null && statsForHand[actionSlot] != null) {
               ranges[1].applyCardPattern(statsForHand[actionSlot].g, statsForHand[actionSlot].bucketFraction() * statsForHand[actionSlot].b);
               ranges[0].applyCardPattern(statsForHand[actionSlot].g, statsForHand[actionSlot].bucketFraction());
            }
         }

         ranges[2] = new HandRange(ranges[1]);
         ranges[2].normalize();
         return ranges;
      }
   }

   private static HandRange createEmptyHandRange() {
      return (HandRange)(AnalysisPanel.isHoldem() ? new HandRange() : new OmahaHandRange());
   }

   public static HandRange copyHandRange(HandRange source) {
      return (HandRange)(AnalysisPanel.isHoldem() ? new HandRange(source) : new OmahaHandRange(source));
   }

   private static HandRange createHandRangeFromWeights(double[] weights) {
      return (HandRange)(AnalysisPanel.isHoldem() ? new HandRange(weights) : new OmahaHandRange(weights));
   }

   public static HandRange buildPlayerRangeWithOptionalEVs(int playerIndex, GameState state, card[] boardCards, HandRange filter, boolean computeEVs) {
      GameState rootState = new GameState(gameState);
      HandRange playerRange;
      if (AnalysisPanel.getTreeStage() == 0) {
         (playerRange = createEmptyHandRange()).fillAllWeights();
      } else {
         playerRange = createHandRangeFromWeights(playerStrategyWeights[playerIndex]);
      }

      playerRange.removeKnownCards(boardCards);
      if (filter != null) {
         playerRange = playerRange.intersect(filter);
      }

      Stack actionStack = new Stack();
      state.pushActionTypes(actionStack);
      int holeCardCount = AnalysisPanel.getHoleCardCount();
      card[] combinedCards;
      if (boardCards == null) {
         combinedCards = new card[holeCardCount];
      } else {
         combinedCards = new card[boardCards.length + holeCardCount];

         for(int i = 0; i < boardCards.length; ++i) {
            combinedCards[i + holeCardCount] = boardCards[i];
         }
      }

      card[] sampleHoleCards = new card[holeCardCount];

      int actionIdx;
      for(actionIdx = 0; actionIdx < sampleHoleCards.length; ++actionIdx) {
         sampleHoleCards[actionIdx] = new card(1, 1);
      }

      applyActionStackToHandRange(playerIndex, actionStack, playerRange, rootState, combinedCards, sampleHoleCards);
      if (!computeEVs) {
         return playerRange;
      } else {
         playerRange.evs = new double[playerRange.weights.length];
         actionIdx = holeCardCount;
         if (state.parentNode.gameStage == 1) {
            actionIdx = holeCardCount + 3;
         } else if (state.parentNode.gameStage == 2) {
            actionIdx = holeCardCount + 4;
         } else if (state.parentNode.gameStage == 3) {
            actionIdx = holeCardCount + 5;
         }

         card[] evalCards = new card[actionIdx];

         int fillIdx;
         for(fillIdx = holeCardCount; fillIdx < actionIdx; ++fillIdx) {
            evalCards[fillIdx] = combinedCards[fillIdx];
         }

         int[] availableActions = getAvailableActions(state.parentNode);
         actionIdx = -1;
         int actionCounter = 0;
         int[] actionsRef = availableActions;
         int bucketIndex = availableActions.length;

         for(int actionSearchIdx = 0; actionSearchIdx < bucketIndex; ++actionSearchIdx) {
            if (actionsRef[actionSearchIdx] == state.nodeType) {
               actionIdx = actionCounter;
               break;
            }

            ++actionCounter;
         }

         for(fillIdx = 0; fillIdx < playerRange.evs.length; ++fillIdx) {
            combinedCards = playerRange.decodeComboIndex(fillIdx, sampleHoleCards);

            for(bucketIndex = 0; bucketIndex < holeCardCount; ++bucketIndex) {
               evalCards[bucketIndex] = combinedCards[bucketIndex];
            }

            if ((bucketIndex = computeBucketForHand(playerIndex, evalCards, state.parentNode.gameStage)) >= 0) {
               double evValue = getEV((Integer)gameStateToNodeId.get(state.parentNode), bucketIndex, actionIdx);
               playerRange.evs[fillIdx] = evValue;
            }
         }

         return playerRange;
      }
   }

   public static HandRange buildPlayerRangeWeightedByStrategy(int playerIndex, GameState childState, card[] boardCards, HandRange filterRange) {
      new GameState(gameState);
      HandRange playerRange;
      if (AnalysisPanel.getTreeStage() == 0) {
         (playerRange = createEmptyHandRange()).fillAllWeights();
      } else {
         playerRange = createHandRangeFromWeights(playerStrategyWeights[playerIndex]);
      }

      playerRange.removeKnownCards(boardCards);
      if (filterRange != null) {
         playerRange = playerRange.intersect(filterRange);
      }

      int holeCardCount;
      int totalCardCount = holeCardCount = AnalysisPanel.getHoleCardCount();
      if (childState.parentNode.gameStage == 1) {
         totalCardCount += 3;
      } else if (childState.parentNode.gameStage == 2) {
         totalCardCount += 4;
      } else if (childState.parentNode.gameStage == 3) {
         totalCardCount += 5;
      }

      card[] holeCardPlaceholders = new card[holeCardCount];

      for(int i = 0; i < holeCardPlaceholders.length; ++i) {
         holeCardPlaceholders[i] = new card(1, 1);
      }

      card[] combinedCards = new card[totalCardCount];

      int fillIdx;
      for(fillIdx = holeCardCount; fillIdx < totalCardCount; ++fillIdx) {
         combinedCards[fillIdx] = boardCards[fillIdx - holeCardCount];
      }

      fillIdx = getNodeId(childState.parentNode);
      int[] availableActions = getAvailableActions(childState.parentNode);
      totalCardCount = -1;
      int targetActionType = childState.nodeType;
      int actionCounter = 0;
      int[] actionsIter = availableActions;
      int actionsLen = availableActions.length;

      for(int ai = 0; ai < actionsLen; ++ai) {
         if (actionsIter[ai] == targetActionType) {
            totalCardCount = actionCounter;
            break;
         }

         ++actionCounter;
      }

      for(int handIdx = 0; handIdx < playerRange.weights.length; ++handIdx) {
         if (playerRange.weights[handIdx] > 0.0D) {
            card[] handCards = playerRange.decodeComboIndex(handIdx, holeCardPlaceholders);

            for(actionsLen = 0; actionsLen < holeCardCount; ++actionsLen) {
               combinedCards[actionsLen] = handCards[actionsLen];
            }

            if ((actionsLen = computeBucketForHand(playerIndex, combinedCards, childState.parentNode.gameStage)) < 0) {
               playerRange.weights[handIdx] = 0.0D;
            } else {
               double freq = getStrategyFrequency(fillIdx, actionsLen, totalCardCount);
               playerRange.weights[handIdx] = freq;
            }
         }
      }

      return playerRange;
   }

   private static HandRange applyActionStackToHandRange(int playerIndex, Stack actionStack, HandRange range, GameState currentState, card[] boardCardsBuffer, card[] tempHoleBuffer) {
      while(!actionStack.isEmpty()) {
         int action = (Integer)actionStack.pop();
         int holeCardCount = AnalysisPanel.getHoleCardCount();
         if (currentState.firstPlayerToAct == playerIndex) {
            int[] availableActions = getAvailableActions(currentState);
            int actionIndex = -1;
            int bucketIdx = 0;
            int[] availAlias = availableActions;
            int nodeId = availableActions.length;

            for(int availInnerIter = 0; availInnerIter < nodeId; ++availInnerIter) {
               if (availAlias[availInnerIter] == action) {
                  actionIndex = bucketIdx;
                  break;
               }

               ++bucketIdx;
            }

            int boardBufferLen = holeCardCount;
            if (currentState.gameStage == 1) {
               boardBufferLen = holeCardCount + 3;
            } else if (currentState.gameStage == 2) {
               boardBufferLen = holeCardCount + 4;
            } else if (currentState.gameStage == 3) {
               boardBufferLen = holeCardCount + 5;
            }

            card[] streetBoardBuffer = new card[boardBufferLen];

            for(nodeId = holeCardCount; nodeId < boardBufferLen && nodeId < boardCardsBuffer.length; ++nodeId) {
               streetBoardBuffer[nodeId] = boardCardsBuffer[nodeId];
            }

            nodeId = getNodeId(currentState);

            for(int handIdx = 0; handIdx < range.weights.length; ++handIdx) {
               if (range.weights[handIdx] > 0.0D) {
                  card[] holeCards = range.decodeComboIndex(handIdx, tempHoleBuffer);

                  for(bucketIdx = 0; bucketIdx < holeCardCount; ++bucketIdx) {
                     streetBoardBuffer[bucketIdx] = holeCards[bucketIdx];
                  }

                  if ((bucketIdx = computeBucketForHand(playerIndex, streetBoardBuffer, currentState.gameStage)) < 0) {
                     range.weights[handIdx] = 0.0D;
                  } else {
                     double frequency = getStrategyFrequency(nodeId, bucketIdx, actionIndex);
                     double[] rangeWeights = range.weights;
                     rangeWeights[handIdx] *= frequency;
                  }
               }
            }
         }

         currentState = getChildState(currentState, action);
         boardCardsBuffer = boardCardsBuffer;
         currentState = currentState;
         range = range;
         actionStack = actionStack;
         playerIndex = playerIndex;
      }

      return range;
   }

   public static double getActionFrequency(int nodeId, int actionIndex) {
      double[] avgStrategy = avg[nodeId];
      nodeId = nodeActionCounts[nodeId];
      double actionSum = 0.0D;

      for(int i = actionIndex; i < avgStrategy.length; i += nodeId) {
         actionSum += avgStrategy[i];
      }

      double totalSum = 0.0D;

      for(actionIndex = actionIndex; actionIndex < avgStrategy.length; ++actionIndex) {
         totalSum += avgStrategy[actionIndex];
      }

      return totalSum > 0.0D ? actionSum / totalSum : 1.0D / (double)nodeId;
   }

   public static JTable createBoardOverviewTable(final GameState state) {
      int[] availableActions = getAvailableActions(state);
      int rootNodeId = getNodeId(state);
      String[] columnHeaders;
      (columnHeaders = new String[availableActions.length + 1])[0] = "Board";

      for(int i = 1; i < columnHeaders.length; ++i) {
         columnHeaders[i] = solver.BetType.format(availableActions[i - 1]);
      }

      final CustomTableModel tableModel = new CustomTableModel(columnHeaders, 0);
      (new Thread(() -> {
         int[] betSizes = solver.MainTabbedPane.getActionPathIds();
         solver.card.parseCards(solver.MainTabbedPane.getBoardCardsString());
         if (state.gameStage == 1) {
            List turnCandidates;
            if (AnalysisPanel.gameType == 3) {
               turnCandidates = solver.CardArrays.dShortdeck(new card[0]);
            } else {
               turnCandidates = solver.CardArrays.getAllCanonicalFlops(new card[0]);
            }

            int flopIdx = 0;
            Object[][] tableRows = new Object[turnCandidates.size()][columnHeaders.length];
            double[] actionTotals = new double[availableActions.length];

            int[] flopWeights;
            if (AnalysisPanel.gameType == 3) {
               flopWeights = solver.CardArrays.eShortdeck();
            } else {
               flopWeights = solver.CardArrays.computeFlopWeights();
            }

            ArrayList workerThreads = new ArrayList();

            Iterator flopIter;
            int flopWeight;
            List flopList;
            if (AnalysisPanel.gameType == 3) {
               flopList = solver.CardArrays.generateShortDeckFlops();
            } else {
               flopList = solver.CardArrays.generateFullDeckFlops();
            }

            for(flopIter = flopList.iterator(); flopIter.hasNext(); ++flopIdx) {
               card[] flop = (card[])flopIter.next();
               tableRows[flopIdx][0] = new ComparableCardArray(flop);
               flopWeight = flopWeights[flopIdx];
               String[] boardStrs = new String[]{flop[0].toString(), flop[1].toString(), flop[2].toString()};
               if (workerThreads.size() >= 4) {
                  label70:
                  while(true) {
                     Iterator aliveIter = workerThreads.iterator();

                     while(aliveIter.hasNext()) {
                        Thread candidateThread;
                        if (!(candidateThread = (Thread)aliveIter.next()).isAlive()) {
                           workerThreads.remove(candidateThread);
                           break label70;
                        }
                     }

                     Thread.yield();
                  }
               }

               Object[] row = tableRows[flopIdx];

               double flopWeightD = (double)flopWeight;

               Thread worker;
               (worker = new Thread(() -> {
                  int[] handRanks;
                  int gameStageLocal;
                  int boardCardCount;
                  double[] evResult;
                  int[] textureIdx;
                  card[] optimizedBoard;
                  int handComboIdx;
                  int bucketIdx;
                  card[] sevenCards;
                  if (AnalysisPanel.isHoldem()) {
                     /*boardStrs = boardStrs;
                     state = state;
                     betSizes = betSizes;*/
                     int numActions;
                     double[] sumWeightedStrat = new double[numActions = getAvailableActions(state).length];
                     double[] strategyOut = new double[numActions];
                     handRanks = new int[4];
                     collections.LongIntHashMap bucketMap = null;
                     HandRange handRange;
                     if (gameState.gameStage > 0) {
                        bucketMap = postflopBucketMaps[state.firstPlayerToAct];
                        handRange = new HandRange(playerStrategyWeights[state.firstPlayerToAct]);
                     } else {
                        (handRange = new HandRange()).fillAllWeights();
                     }

                     int nodeId = getNodeId(state);
                     if ((gameStageLocal = state.gameStage) == 0) {
                        boardCardCount = 2;
                     } else if (gameStageLocal == 1) {
                        boardCardCount = 5;
                     } else if (gameStageLocal == 2) {
                        boardCardCount = 6;
                     } else {
                        boardCardCount = 7;
                     }

                     card[] boardOnly = new card[boardCardCount - 2];

                     for(int i = 0; i < boardCardCount - 2; ++i) {
                        boardOnly[i] = solver.card.parseCard(boardStrs[i]);
                     }

                     sevenCards = new card[7];

                     for(int i = 0; i < 7; ++i) {
                        sevenCards[i] = new card(1, 1);
                     }

                     card[] holeAndBoard = combine2CardsWithArray(new card[]{new card(1, 1), new card(1, 1)}, boardOnly);
                     textureIdx = new int[3];
                     if (gameState.gameStage == 0 && boardOnly.length > 0) {
                        optimizedBoard = CardCombinations.optimizeSuits(boardOnly);
                        textureIdx[0] = holeAndBoard.length > 2 ? flopTexture.get(CardCombinations.getCardArrayNumValue(optimizedBoard, 3)) : 0;
                        textureIdx[1] = holeAndBoard.length > 5 ? turnTexture.get(CardCombinations.getCardArrayNumValue(optimizedBoard, 4)) : 0;
                        textureIdx[2] = holeAndBoard.length > 6 ? riverTexture.get(CardCombinations.getCardArrayNumValue(optimizedBoard, 5)) : 0;
                     }

                     double[] filterWeights = null;
                     if (EquitySortComparator.activeFilter != null) {
                        System.lineSeparator();
                        filterWeights = EquitySortComparator.activeFilter.weights;
                     }

                     for(handComboIdx = 0; handComboIdx < 1326; ++handComboIdx) {
                        if (handRange.weights[handComboIdx] > 1.0E-7D && (filterWeights == null || filterWeights[handComboIdx] > 1.0E-7D)) {
                           handRange.decodeComboIndex(handComboIdx, holeAndBoard);
                           if (!solver.Equity.cardInArray(holeAndBoard[0], boardOnly) && !solver.Equity.cardInArray(holeAndBoard[1], boardOnly)) {
                              bucketIdx = computeHoldemBucketAndReachProb(betSizes, strategyOut, handRange.weights[handComboIdx], state, holeAndBoard, sevenCards, handRanks, bucketMap, textureIdx);
                              double handWeight = strategyOut[0];
                              getNormalizedStrategy(nodeId, bucketIdx, strategyOut);

                              for(int actionIdx = 0; actionIdx < numActions; ++actionIdx) {
                                 double contribution = handWeight * strategyOut[actionIdx];
                                 sumWeightedStrat[actionIdx] += contribution;
                              }
                           }
                        }
                     }

                     evResult = sumWeightedStrat;
                  } else {
                     //boardStrs = boardStrs;
                     //state = state;
                     int numActions = getAvailableActions(state).length;
                     handRanks = new int[4];
                     int[] handRanks2 = new int[4];
                     int[] rankCounts = new int[13];
                     double[] rangeWeights = OmahaHandRange.getDefaultRange().weights;
                     collections.LongIntHashMap bucketMap = null;
                     if (gameState.gameStage > 0) {
                        bucketMap = postflopBucketMaps[state.firstPlayerToAct];
                        rangeWeights = playerStrategyWeights[state.firstPlayerToAct];
                     }

                     int nodeId = getNodeId(state);
                     int numHoleCards4 = AnalysisPanel.is5Card() ? 5 : 4;
                     if ((gameStageLocal = state.gameStage) == 0) {
                        boardCardCount = numHoleCards4;
                     } else if (gameStageLocal == 1) {
                        boardCardCount = numHoleCards4 + 3;
                     } else if (gameStageLocal == 2) {
                        boardCardCount = numHoleCards4 + 4;
                     } else {
                        boardCardCount = numHoleCards4 + 5;
                     }

                     int boardStartIdx;
                     if (gameState.gameStage == 0) {
                        boardStartIdx = numHoleCards4;
                     } else if (gameState.gameStage == 1) {
                        boardStartIdx = numHoleCards4 + 3;
                     } else if (gameState.gameStage == 2) {
                        boardStartIdx = numHoleCards4 + 4;
                     } else {
                        boardStartIdx = numHoleCards4 + 5;
                     }

                     sevenCards = new card[boardCardCount];
                     int[] handRankScratch10 = new int[10];
                     textureIdx = new int[boardCardCount];

                     int idx;
                     for(idx = 0; idx < textureIdx.length; ++idx) {
                        textureIdx[idx] = -1;
                     }

                     for(idx = 0; idx < numHoleCards4; ++idx) {
                        sevenCards[idx] = new card(1, 1);
                     }

                     optimizedBoard = new card[boardCardCount - numHoleCards4];

                     for(handComboIdx = 0; handComboIdx < boardCardCount - numHoleCards4; ++handComboIdx) {
                        sevenCards[handComboIdx + numHoleCards4] = solver.card.parseCard(boardStrs[handComboIdx]);
                        optimizedBoard[handComboIdx] = solver.card.parseCard(boardStrs[handComboIdx]);
                        textureIdx[handComboIdx + numHoleCards4] = sevenCards[handComboIdx + numHoleCards4].getFullDeckIndex52();
                     }

                     int[] textureIdxOmaha = new int[3];
                     if (gameState.gameStage == 0 && optimizedBoard.length > 0) {
                        card[] optimizedBoardOmaha = CardCombinations.optimizeSuits(optimizedBoard);
                        textureIdxOmaha[0] = optimizedBoard.length > 0 ? flopTexture.get(CardCombinations.getCardArrayNumValue(optimizedBoardOmaha, 3)) : 0;
                        textureIdxOmaha[1] = optimizedBoard.length > 3 ? turnTexture.get(CardCombinations.getCardArrayNumValue(optimizedBoardOmaha, 4)) : 0;
                        textureIdxOmaha[2] = optimizedBoard.length > 4 ? riverTexture.get(CardCombinations.getCardArrayNumValue(optimizedBoardOmaha, 5)) : 0;
                     }

                     double[] filterWeights = null;
                     if (EquitySortComparator.activeFilter != null) {
                        System.lineSeparator();
                        filterWeights = EquitySortComparator.activeFilter.weights;
                     }

                     double[] strategyOut = new double[numActions];
                     double[] sumWeightedStrat = new double[numActions];
                     int[] playerActionList;
                     int[] actionSeqBuf = new int[(playerActionList = new int[betSizes.length]).length];
                     int playerActionCount = collectPlayerActionIndices(playerActionList, actionSeqBuf, betSizes, state.firstPlayerToAct);
                     playerActionList = Arrays.copyOf(playerActionList, playerActionCount);
                     int numHoleCardsInner = AnalysisPanel.is5Card() ? 5 : 4;
                     int omahaComboCount = AnalysisPanel.is5Card() ? 2598960 : 270725;

                     label195:
                     for(int comboIdx = 0; comboIdx < omahaComboCount; ++comboIdx) {
                        if (rangeWeights[comboIdx] > 1.0E-7D && (filterWeights == null || filterWeights[comboIdx] > 1.0E-7D)) {
                           idx = AnalysisPanel.is5Card() ? OmahaHandRange.indexToComboEncoding5c[comboIdx] : OmahaHandRange.indexToComboEncoding4c[comboIdx];

                           int bucketIdxOmaha;
                           for(bucketIdxOmaha = 0; bucketIdxOmaha < numHoleCardsInner; ++bucketIdxOmaha) {
                              textureIdx[bucketIdxOmaha] = idx % 52;

                              for(int boardClashIdx = boardStartIdx; boardClashIdx < boardCardCount; ++boardClashIdx) {
                                 if (textureIdx[bucketIdxOmaha] == textureIdx[boardClashIdx]) {
                                    continue label195;
                                 }
                              }

                              idx /= 52;
                           }

                           bucketIdxOmaha = computeOmahaBucketAndReachProb(playerActionList, actionSeqBuf, strategyOut, rangeWeights[comboIdx], state, textureIdx, sevenCards, textureIdxOmaha, bucketMap, gameStageLocal, handRankScratch10, rankCounts, handRanks2, handRanks);
                           double handWeight = strategyOut[0];
                           getNormalizedStrategy(nodeId, bucketIdxOmaha, strategyOut);

                           for(idx = 0; idx < numActions; ++idx) {
                              sumWeightedStrat[idx] += handWeight * strategyOut[idx];
                           }
                        }
                     }

                     evResult = sumWeightedStrat;
                  }

                  double[] evResultFinal = evResult;
                  double evTotal = 0.0D;

                  for(int i = 0; i < evResultFinal.length; ++i) {
                     evTotal += evResultFinal[i];
                  }

                  synchronized(actionTotals) {
                     bucketIdx = 1;

                     while(true) {
                        if (bucketIdx >= row.length) {
                           break;
                        }

                        String percentStr = formatAsPercent(evResultFinal[bucketIdx - 1] / evTotal);
                        row[bucketIdx] = new RangeButtonListener(percentStr);
                        actionTotals[bucketIdx - 1] += flopWeightD * evResultFinal[bucketIdx - 1];
                        ++bucketIdx;
                     }
                  }

                  SwingUtilities.invokeLater(() -> {
                     tableModel.addRow(row);
                  });
               })).start();

               workerThreads.add(worker);
            }

            flopIter = workerThreads.iterator();

            while(flopIter.hasNext()) {
               Thread workerThread = (Thread)flopIter.next();

               try {
                  workerThread.join();
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }

            double grandTotal = 0.0D;

            for(flopWeight = 0; flopWeight < actionTotals.length; ++flopWeight) {
               grandTotal += actionTotals[flopWeight];
            }

            for(flopWeight = 1; flopWeight < columnHeaders.length; ++flopWeight) {
               columnHeaders[flopWeight] = columnHeaders[flopWeight] + " (" + formatAsPercent(actionTotals[flopWeight - 1] / grandTotal) + ")";
            }

            tableModel.setColumnIdentifiers(columnHeaders);
         } else {
            card[] textureIdx;

            List optimizedBoard;
            if (AnalysisPanel.gameType == 3) {
               optimizedBoard = solver.CardArrays.getAllPossibleArraysPlus1ShortDeck((card[])Arrays.copyOfRange(textureIdx = solver.card.parseCards(solver.MainTabbedPane.getBoardCardsString()), 0, textureIdx.length - 1));
            } else {
               optimizedBoard = solver.CardArrays.getAllPossibleArraysPlus1FullDeck((card[])Arrays.copyOfRange(textureIdx = solver.card.parseCards(solver.MainTabbedPane.getBoardCardsString()), 0, textureIdx.length - 1));
            }


            ArrayList boardScratch;
            (boardScratch = new ArrayList()).addAll(solver.MainTabbedPane.enteredBoard);
            Object[][] tableRows = new Object[optimizedBoard.size()][columnHeaders.length];
            int rowIdx = 0;

            for(Iterator cardIter = optimizedBoard.iterator(); cardIter.hasNext(); ++rowIdx) {
               card nextCard = (card)cardIter.next();
               boardScratch.remove(boardScratch.size() - 1);
               boardScratch.add(nextCard.toString());
               Object[] rowStats = buildBucketGeneratorHoldem((GameStateRefreshTask)null, (GameState)state, (ArrayList)boardScratch).bucketLabels;
               tableRows[rowIdx][0] = new ComparableCardArray(new card[]{nextCard});

               for(int col = 1; col < tableRows[rowIdx].length; ++col) {
                  tableRows[rowIdx][col] = new RangeButtonListener((String)rowStats[col - 1]);
               }

               int rowIdxFinal = rowIdx;
               SwingUtilities.invokeLater(() -> {
                  tableModel.addRow(tableRows[rowIdxFinal]);
               });
            }

         }
      })).start();
      JTable table;
      (table = new JTable(tableModel)).setDefaultRenderer(ComparableCardArray.class, new CustomCellRenderer());
      table.getTableHeader().setDefaultRenderer(ThemeManager.createTableHeaderRenderer());
      table.getTableHeader().setOpaque(true);
      table.getTableHeader().setBackground(ThemeManager.TABLE_HEADER_BG);
      table.getTableHeader().setForeground(ThemeManager.TEXT_PRIMARY);
      table.setRowHeight((int)(25.0F * solver.PokerSolverMain.c));
      TableRowSorter rowSorter = new TableRowSorter(tableModel);
      table.setRowSorter(rowSorter);
      table.setRowSelectionAllowed(true);
      ArrayList sortKeys;
      (sortKeys = new ArrayList()).add(new SortKey(0, SortOrder.ASCENDING));
      rowSorter.setSortKeys(sortKeys);
      rowSorter.sort();
      return table;
   }

   private static ArrayList buildPlayerHandStatistics(GameState state, ArrayList boardStrs) {
      ArrayList results = new ArrayList();
      double[] equities = null;
      collections.LongIntHashMap bucketMap = null;
      card[][] hands;
      if (gameState.gameStage > 0) {
         equities = playerEquities[state.firstPlayerToAct];
         hands = playerHands[state.firstPlayerToAct];
         bucketMap = postflopBucketMaps[state.firstPlayerToAct];
      } else {
         List startingHands;
         if (AnalysisPanel.isHoldem()) {
            if (AnalysisPanel.gameType == 0) {
               startingHands = solver.CardArrays.getStartingHandsListHoldem();
            } else {
               startingHands = solver.CardArrays.getStartingHandsListHoldemShortdeck();
            }
         } else {
            startingHands = solver.CardArrays.getStartingHandsListOmaha();
         }

         hands = new card[startingHands.size()][];
         hands = (card[][])startingHands.toArray(hands);
      }

      card[] boardCards = new card[boardStrs.size()];
      int boardIdx = 0;

      for(Iterator boardIter = boardStrs.iterator(); boardIter.hasNext(); ++boardIdx) {
         String cardStr = (String)boardIter.next();
         boardCards[boardIdx] = solver.card.parseCard(cardStr);
      }

      if (gameState.gameStage == 0 && boardCards.length > 2) {
         CardCombinations.sort3Cards(boardCards);
      }

      int numActions = getAvailableActions(state).length;
      int handIdx = -1;
      card[] scratchCards = new card[7];

      for(int i = 0; i < 7; ++i) {
         scratchCards[i] = new card(0, 0);
      }

      int[] bucketIdxArr = new int[4];
      int[] betSizes = solver.MainTabbedPane.getActionPathIds();
      card[][] handsArr = hands;
      int handsLen = hands.length;

      for(int handLoopIdx = 0; handLoopIdx < handsLen; ++handLoopIdx) {
         card[] hand = handsArr[handLoopIdx];
         ++handIdx;
         card[] holeCards;
         (holeCards = new card[2])[0] = new card(hand[0]);
         holeCards[1] = new card(hand[1]);
         double frequency = 0.0D;
         double[] strategy = null;
         if (state.gameStage == 0) {
            bucketIdxArr = computeBucketsForHand(new card[]{hand[0], hand[1]}, scratchCards);
            frequency = computeReachProbabilityByPath(betSizes, 1.0D, state.firstPlayerToAct, bucketIdxArr);
            strategy = getStrategyForHand(new GameState(state), bucketIdxArr[0]);
         } else {
            long handKey;
            if (state.gameStage == 1) {
               if (hand[0].cardEquals(boardCards[0]) || hand[0].cardEquals(boardCards[1]) || hand[0].cardEquals(boardCards[2]) || hand[1].cardEquals(boardCards[0]) || hand[1].cardEquals(boardCards[1]) || hand[1].cardEquals(boardCards[2])) {
                  continue;
               }

               hand = new card[]{hand[0], hand[1], boardCards[0], boardCards[1], boardCards[2]};
               if (gameState.gameStage > 0) {
                  handKey = (new CardCombinations(hand)).computeHash();
                  bucketIdxArr[1] = bucketMap.get(handKey);
                  frequency = computeReachProbabilityByPath(betSizes, equities[handIdx], state.firstPlayerToAct, bucketIdxArr);
                  strategy = getStrategyForHand(new GameState(state), bucketMap.get(handKey));
               } else {
                  bucketIdxArr = computeBucketsForHand(hand, scratchCards);
                  frequency = computeReachProbabilityByPath(betSizes, 1.0D, state.firstPlayerToAct, bucketIdxArr);
                  strategy = getStrategyForHand(new GameState(state), bucketIdxArr[1]);
               }
            } else if (state.gameStage == 2) {
               if (hand[0].cardEquals(boardCards[0]) || hand[0].cardEquals(boardCards[1]) || hand[0].cardEquals(boardCards[2]) || hand[0].cardEquals(boardCards[3]) || hand[1].cardEquals(boardCards[0]) || hand[1].cardEquals(boardCards[1]) || hand[1].cardEquals(boardCards[2]) || hand[1].cardEquals(boardCards[3])) {
                  continue;
               }

               hand = new card[]{hand[0], hand[1], boardCards[0], boardCards[1], boardCards[2], boardCards[3]};
               if (gameState.gameStage > 0) {
                  handKey = (new CardCombinations(hand)).computeHash();
                  bucketIdxArr[1] = bucketMap.get(handKey / 100L);
                  bucketIdxArr[2] = bucketMap.get(handKey);
                  frequency = computeReachProbabilityByPath(betSizes, equities[handIdx], state.firstPlayerToAct, bucketIdxArr);
                  strategy = getStrategyForHand(new GameState(state), bucketMap.get(handKey));
               } else {
                  bucketIdxArr = computeBucketsForHand(hand, scratchCards);
                  frequency = computeReachProbabilityByPath(betSizes, 1.0D, state.firstPlayerToAct, bucketIdxArr);
                  strategy = getStrategyForHand(new GameState(state), bucketIdxArr[2]);
               }
            } else if (state.gameStage == 3) {
               if (hand[0].cardEquals(boardCards[0]) || hand[0].cardEquals(boardCards[1]) || hand[0].cardEquals(boardCards[2]) || hand[0].cardEquals(boardCards[3]) || hand[0].cardEquals(boardCards[4]) || hand[1].cardEquals(boardCards[0]) || hand[1].cardEquals(boardCards[1]) || hand[1].cardEquals(boardCards[2]) || hand[1].cardEquals(boardCards[3]) || hand[1].cardEquals(boardCards[4])) {
                  continue;
               }

               hand = new card[]{hand[0], hand[1], boardCards[0], boardCards[1], boardCards[2], boardCards[3], boardCards[4]};
               if (gameState.gameStage > 0) {
                  handKey = (new CardCombinations(hand)).computeHash();
                  bucketIdxArr[1] = bucketMap.get(handKey / 100L / 100L);
                  bucketIdxArr[2] = bucketMap.get(handKey / 100L);
                  bucketIdxArr[3] = bucketMap.get(handKey);
                  frequency = computeReachProbabilityByPath(betSizes, equities[handIdx], state.firstPlayerToAct, bucketIdxArr);
                  strategy = getStrategyForHand(new GameState(state), bucketMap.get(handKey));
               } else {
                  bucketIdxArr = computeBucketsForHand(hand, scratchCards);
                  frequency = computeReachProbabilityByPath(betSizes, 1.0D, state.firstPlayerToAct, bucketIdxArr);
                  strategy = getStrategyForHand(new GameState(state), bucketIdxArr[3]);
               }
            }
         }

         if (frequency >= 1.0E-4D) {
            PlayerHandStatistic[] statsPerAction = new PlayerHandStatistic[numActions];

            for(int actionIdx = 0; actionIdx < numActions; ++actionIdx) {
               if (strategy[actionIdx] > 0.001D) {
                  statsPerAction[actionIdx] = new PlayerHandStatistic(frequency, holeCards, (int)(10000.0D * strategy[actionIdx]), 0);
               }
            }

            results.add(statsPerAction);
         }
      }

      return results;
   }

   public static BucketGenerator buildBucketGeneratorHoldem(GameStateRefreshTask refreshTaskParam, GameState stateParam, ArrayList boardListParam) {
      PlayerHandStatistic.a = stateParam;
      if (!AnalysisPanel.isHoldem()) {
         return buildBucketGeneratorOmaha(refreshTaskParam, stateParam, boardListParam);
      } else {
         int[] currentActions;
         int holdemNodeId;
         int numActionsOuter;
         double[] auxWeights;

         int startingHandsNum = 1326;
         //if (AnalysisPanel.gameType == 3) { startingHandsNum = 630; }

         if (EquitySortComparator.d != null && EquitySortComparator.d.countBuckets(stateParam.gameStage) > 0) {
            ArrayList boardList = boardListParam;
            GameState state = stateParam;
            GameStateRefreshTask refreshTask = refreshTaskParam;
            numActionsOuter = getAvailableActions(stateParam).length;
            currentActions = solver.MainTabbedPane.getActionPathIds();
            int[] equityHistogram = new int[4];
            collections.LongIntHashMap bucketMap = null;
            HandRange handRange;
            if (gameState.gameStage > 0) {
               bucketMap = postflopBucketMaps[stateParam.firstPlayerToAct];
               handRange = new HandRange(playerStrategyWeights[stateParam.firstPlayerToAct]);
            } else {
               (handRange = new HandRange()).fillAllWeights();
            }

            int nodeId = getNodeId(stateParam);
            double weightedEquitySum = 0.0D;
            byte cardCount;
            if ((holdemNodeId = stateParam.gameStage) == 0) {
               cardCount = 2;
            } else if (holdemNodeId == 1) {
               cardCount = 5;
            } else if (holdemNodeId == 2) {
               cardCount = 6;
            } else {
               cardCount = 7;
            }

            card[] boardCards = new card[cardCount - 2];

            for(int boardIdx = 0; boardIdx < cardCount - 2; ++boardIdx) {
               boardCards[boardIdx] = solver.card.parseCard((String)boardList.get(boardIdx));
            }

            card[] fullHand = new card[7];

            for(int holeFillIdx = 0; holeFillIdx < 7; ++holeFillIdx) {
               fullHand[holeFillIdx] = new card(1, 1);
            }

            card[] combinedCards = combine2CardsWithArray(new card[]{new card(1, 1), new card(1, 1)}, boardCards);
            int[] textureIndices = new int[3];
            if (gameState.gameStage == 0 && boardCards.length > 0) {
               card[] optimizedSuitCards = CardCombinations.optimizeSuits(boardCards);
               textureIndices[0] = combinedCards.length > 2 ? flopTexture.get(CardCombinations.getCardArrayNumValue(optimizedSuitCards, 3)) : 0;
               textureIndices[1] = combinedCards.length > 5 ? turnTexture.get(CardCombinations.getCardArrayNumValue(optimizedSuitCards, 4)) : 0;
               textureIndices[2] = combinedCards.length > 6 ? riverTexture.get(CardCombinations.getCardArrayNumValue(optimizedSuitCards, 5)) : 0;
            }

            double[] filterWeights = null;
            if (EquitySortComparator.activeFilter != null) {
               System.lineSeparator();
               filterWeights = EquitySortComparator.activeFilter.weights;
            }

            double[] actionEquities = new double[numActionsOuter];
            double[] actionEquityTotals = new double[numActionsOuter];
            auxWeights = new double[numActionsOuter];
            double totalWeight = 0.0D;
            ViewSettingsManager viewSettings;
            (viewSettings = EquitySortComparator.d).resetFilterStats(numActionsOuter);

            for(int handIdx = 0; handIdx < startingHandsNum; ++handIdx) {
               boolean filteredOut = false;
               if (handRange.weights[handIdx] > 1.0E-7D) {
                  if (filterWeights != null && filterWeights[handIdx] <= 1.0E-7D) {
                     filteredOut = true;
                  }

                  if (refreshTask != null && refreshTask.cancelled) {
                     return null;
                  }

                  handRange.decodeComboIndex(handIdx, combinedCards);
                  if (!solver.Equity.cardInArray(combinedCards[0], boardCards) && !solver.Equity.cardInArray(combinedCards[1], boardCards)) {
                     if (refreshTask != null && refreshTask.cancelled) {
                        return null;
                     }

                     HandFilterQuery filterQuery = viewSettings.findMatchingFilter(HandFilterParser.m, handIdx, boardCards);
                     int actionsForNode = computeHoldemBucketAndReachProb(currentActions, actionEquities, handRange.weights[handIdx], state, combinedCards, fullHand, equityHistogram, bucketMap, textureIndices);
                     double handWeight = actionEquities[0];
                     getNormalizedStrategy(nodeId, actionsForNode, actionEquities);
                     if (!filteredOut) {
                        filterQuery.aggregateEv += handWeight;
                        weightedEquitySum += handWeight;

                        for(int actionIdx = 0; actionIdx < numActionsOuter; ++actionIdx) {
                           double actionContribution;
                           if ((actionContribution = handWeight * actionEquities[actionIdx]) > 0.0D) {
                              filterQuery.addFrequency(actionIdx, actionContribution);
                              if (readAverageStrategy(nodeId, actionsForNode, auxWeights)) {
                                 double adjustedContribution = actionContribution + 1.0E-10D;
                                 filterQuery.addWeightedEv(actionIdx, auxWeights[actionIdx] * adjustedContribution);
                                 filterQuery.addEvWeight(actionIdx, adjustedContribution);
                              }

                              actionEquityTotals[actionIdx] += actionContribution;
                           }
                        }
                     }

                     totalWeight += handWeight;
                  }
               }
            }

            solver.MainTabbedPane.updateFilterPercent(weightedEquitySum / totalWeight);
            return buildFilteredBucketGenerator(refreshTask, viewSettings, actionEquityTotals);
         } else {
            currentActions = solver.MainTabbedPane.getActionPathIds();
            int numActions = getAvailableActions(stateParam).length;
            int[] equityHistogram = new int[4];
            collections.LongIntHashMap bucketMap = null;
            HandRange handRange;
            if (gameState.gameStage > 0) {
               bucketMap = postflopBucketMaps[stateParam.firstPlayerToAct];
               handRange = new HandRange(playerStrategyWeights[stateParam.firstPlayerToAct]);
            } else {
               (handRange = new HandRange()).fillAllWeights();
            }

            holdemNodeId = getNodeId(stateParam);
            double weightedEquitySum = 0.0D;
            int stage;
            byte cardCount;
            if ((stage = stateParam.gameStage) == 0) {
               cardCount = 2;
            } else if (stage == 1) {
               cardCount = 5;
            } else if (stage == 2) {
               cardCount = 6;
            } else {
               cardCount = 7;
            }

            int boardCardCount = Math.min(cardCount - 2, boardListParam.size());
            card[] boardCards = new card[boardCardCount];

            for(int boardIdx = 0; boardIdx < boardCardCount; ++boardIdx) {
               boardCards[boardIdx] = solver.card.parseCard((String)boardListParam.get(boardIdx));
            }

            card[] fullHand = new card[7];

            for(numActionsOuter = 0; numActionsOuter < 7; ++numActionsOuter) {
               fullHand[numActionsOuter] = new card(1, 1);
            }

            card[] combinedCards = combine2CardsWithArray(new card[]{new card(1, 1), new card(1, 1)}, boardCards);
            int[] textureIndices = new int[3];
            if (gameState.gameStage == 0 && boardCards.length > 0) {
               card[] optimizedSuitCards = CardCombinations.optimizeSuits(boardCards);
               textureIndices[0] = combinedCards.length > 2 ? flopTexture.get(CardCombinations.getCardArrayNumValue(optimizedSuitCards, 3)) : 0;
               textureIndices[1] = combinedCards.length > 5 ? turnTexture.get(CardCombinations.getCardArrayNumValue(optimizedSuitCards, 4)) : 0;
               textureIndices[2] = combinedCards.length > 6 ? riverTexture.get(CardCombinations.getCardArrayNumValue(optimizedSuitCards, 5)) : 0;
            }

            auxWeights = null;
            if (EquitySortComparator.activeFilter != null) {
               System.lineSeparator();
               auxWeights = EquitySortComparator.activeFilter.weights;
            }

            double[] actionEquities = new double[numActions];
            double[] strategyBuffer = new double[numActions];
            double[] actionEquityTotals = new double[numActions];
            double totalWeight = 0.0D;
            ArrayList[] actionHandStats = new ArrayList[numActions];

            for(int initIdx = 0; initIdx < numActions; ++initIdx) {
               actionHandStats[initIdx] = new ArrayList();
            }

            HashMap equityGenByCanon = new HashMap();

            for(int handIdx = 0; handIdx < startingHandsNum; ++handIdx) {
               boolean filteredOut = false;
               if (handRange.weights[handIdx] > 1.0E-7D) {
                  if (auxWeights != null && auxWeights[handIdx] <= 1.0E-7D) {
                     filteredOut = true;
                  }

                  if (refreshTaskParam != null && refreshTaskParam.cancelled) {
                     return null;
                  }

                  handRange.decodeComboIndex(handIdx, combinedCards);
                  if (!solver.Equity.cardInArray(combinedCards[0], boardCards) && !solver.Equity.cardInArray(combinedCards[1], boardCards)) {
                     if (refreshTaskParam != null && refreshTaskParam.cancelled) {
                        return null;
                     }

                     CardCombinations cardCombos;
                     long canonKey = (cardCombos = new CardCombinations(combinedCards)).computeHash();
                     card[] canonicalBoard = solver.card.parseCards(stage == 0 ? cardCombos.toString() : cardCombos.handStructureKey);
                     int actionsForNode = computeHoldemBucketAndReachProb(currentActions, actionEquities, handRange.weights[handIdx], stateParam, combinedCards, fullHand, equityHistogram, bucketMap, textureIndices);
                     double handWeight = actionEquities[0];
                     getNormalizedStrategy(holdemNodeId, actionsForNode, actionEquities);
                     if (!filteredOut) {
                        weightedEquitySum += handWeight;
                        if (!equityGenByCanon.containsKey(canonKey)) {
                           equityGenByCanon.put(canonKey, new OmahaEquityGenerator(numActions));
                        }

                        OmahaEquityGenerator equityGen = (OmahaEquityGenerator)equityGenByCanon.get(canonKey);

                        for(int actionIdx = 0; actionIdx < numActions; ++actionIdx) {
                           double actionContribution = handWeight * actionEquities[actionIdx];
                           if (equityGen.actionStats[actionIdx] == null) {
                              equityGen.actionStats[actionIdx] = new PlayerHandStatistic(0.0D, canonicalBoard, 0, actionsForNode);
                              actionHandStats[actionIdx].add(equityGen.actionStats[actionIdx]);
                           }

                           if (actionContribution > 0.0D) {
                              equityGen.addEquity(actionIdx, actionContribution);
                              actionEquityTotals[actionIdx] += actionContribution;
                           }

                           if (readAverageStrategy(holdemNodeId, actionsForNode, strategyBuffer)) {
                              double adjustedContribution = actionContribution + 1.0E-10D;
                              equityGen.addStrategy(actionIdx, strategyBuffer[actionIdx] * adjustedContribution);
                              equityGen.addWeight(actionIdx, adjustedContribution);
                           }
                        }
                     }

                     totalWeight += handWeight;
                  }
               }
            }

            solver.MainTabbedPane.updateFilterPercent(weightedEquitySum / totalWeight);
            Iterator genIter = equityGenByCanon.values().iterator();

            while(genIter.hasNext()) {
               ((OmahaEquityGenerator)genIter.next()).finalizeStats();
            }

            normalizeInPlace(actionEquityTotals);
            Object[][] actionHandStatArrays = new Object[numActions][];
            String[] actionPercentages = new String[numActions];

            for(int outActionIdx = 0; outActionIdx < numActions; ++outActionIdx) {
               actionPercentages[outActionIdx] = formatAsPercent(actionEquityTotals[outActionIdx]);
               if (GameSettings.l) {
                  actionHandStats[outActionIdx].removeIf((stat) -> {
                     return (double)((PlayerHandStatistic)stat).c < 10000.0D * GameSettings.k;
                  });
               }

               actionHandStatArrays[outActionIdx] = new Object[actionHandStats[outActionIdx].size()];
               Collections.sort(actionHandStats[outActionIdx], PlayerHandStatistic.getActiveComparator());
               int outIdx = 0;

               PlayerHandStatistic stat;
               for(Iterator statIter = actionHandStats[outActionIdx].iterator(); statIter.hasNext(); actionHandStatArrays[outActionIdx][outIdx++] = stat) {
                  stat = (PlayerHandStatistic)statIter.next();
               }
            }

            return new BucketGenerator(actionPercentages, actionHandStatArrays);
         }
      }
   }

   private static BucketGenerator buildFilteredBucketGenerator(GameStateRefreshTask refreshTask, ViewSettingsManager viewSettings, double[] bucketWeights) {
      int numBuckets = bucketWeights.length;
      double totalWeight = 0.0D;
      double[] weightsIter = bucketWeights;
      int weightsLen = bucketWeights.length;

      int bucketIdx;
      for(bucketIdx = 0; bucketIdx < weightsLen; ++bucketIdx) {
         double weight = weightsIter[bucketIdx];
         totalWeight += weight;
      }

      viewSettings.normalizeFilterStats();
      Object[][] bucketFilters = new Object[numBuckets][];
      String[] bucketLabels = new String[numBuckets];

      label53:
      for(bucketIdx = 0; bucketIdx < numBuckets; ++bucketIdx) {
         if (refreshTask != null && refreshTask.cancelled) {
            return null;
         }

         bucketLabels[bucketIdx] = formatAsPercent(bucketWeights[bucketIdx] / totalWeight);
         ArrayList filterList = viewSettings.getSortedFiltersForBucket(bucketIdx);
         int filterCount = 0;
         Iterator filterIter = filterList.iterator();

         while(true) {
            HandFilterQuery filter;
            do {
               if (!filterIter.hasNext()) {
                  bucketFilters[bucketIdx] = new Object[filterCount];
                  filterCount = 0;
                  filterIter = filterList.iterator();

                  while(true) {
                     do {
                        if (!filterIter.hasNext()) {
                           continue label53;
                        }

                        filter = (HandFilterQuery)filterIter.next();
                     } while(GameSettings.l && filter.frequencies[bucketIdx] <= 0.01D);

                     bucketFilters[bucketIdx][filterCount] = filter;
                     ++filterCount;
                  }
               }

               filter = (HandFilterQuery)filterIter.next();
            } while(GameSettings.l && filter.frequencies[bucketIdx] <= 0.01D);

            ++filterCount;
         }
      }

      return new BucketGenerator(bucketLabels, bucketFilters);
   }

   private static int computeOmahaBucketAndReachProb(int[] pathNodeIds, int[] pathActionIdxs, double[] reachProbOut, double baseReachProb, GameState state, int[] cardInts, card[] holeCardsOut, int[] textureIndices, collections.LongIntHashMap bucketMap, int stage, int[] sortedBuffer, int[] lowHandBuffer, int[] suitBuffer, int[] bucketsOut) {
      if (gameState.gameStage == 0) {
         int holeIdx;
         int numHoleCards = AnalysisPanel.is5Card() ? 5 : 4;
         for(holeIdx = 0; holeIdx < numHoleCards; ++holeIdx) {
            holeCardsOut[holeIdx].rank = cardInts[holeIdx] % 13 + 2;
            if (holeCardsOut[holeIdx].rank == 14) {
               holeCardsOut[holeIdx].rank = 1;
            }

            holeCardsOut[holeIdx].suit = cardInts[holeIdx] / 13;
         }

         if (stage >= 3) {
            holeIdx = AnalysisPanel.is5Card() ? handeval.PloHandEvaluator.evaluatePlo5Hi(cardInts) : handeval.PloHandEvaluator.evaluatePlo4Hi(cardInts);
            if (AnalysisPanel.gameType == 2) {
               holeIdx |= handeval.PloHandEvaluator.evaluateLowHand(cardInts, lowHandBuffer) << 16;
            }
         } else {
            holeIdx = 0;
         }

         bucketsOut = computeOmahaBucketsFromInts(bucketsOut, textureIndices, cardInts, sortedBuffer, suitBuffer, holeIdx, holeCardsOut.length);
         reachProbOut[0] = computeReachProbabilityByNodes(pathNodeIds, pathActionIdxs, 1.0D, bucketsOut);
         return bucketsOut[stage];
      } else {
         int numHoleCards = AnalysisPanel.is5Card() ? 5 : 4;
         if (stage == 1) {
            int flopLen = numHoleCards + 3;
            bucketsOut[1] = bucketMap.get(AnalysisPanel.is5Card() ? OmahaHandNormalizer.normalizeMulti5c(cardInts, sortedBuffer, flopLen, suitBuffer, isoLevel) : OmahaHandNormalizer.normalizeMulti(cardInts, sortedBuffer, 7, suitBuffer, isoLevel));
         } else {
            long turnHash;
            int turnLen = numHoleCards + 4;
            int riverLen = numHoleCards + 5;
            if (stage == 2) {
               turnHash = AnalysisPanel.is5Card() ? OmahaHandNormalizer.normalizeMulti5c(cardInts, sortedBuffer, turnLen, suitBuffer, isoLevel) : OmahaHandNormalizer.normalizeMulti(cardInts, sortedBuffer, 8, suitBuffer, isoLevel);
               bucketsOut[2] = bucketMap.get(turnHash);
               bucketsOut[1] = bucketMap.get(turnHash / 100L);
            } else if (stage == 3) {
               turnHash = AnalysisPanel.is5Card() ? OmahaHandNormalizer.normalizeMulti5c(cardInts, sortedBuffer, turnLen, suitBuffer, isoLevel) : OmahaHandNormalizer.normalizeMulti(cardInts, sortedBuffer, 8, suitBuffer, isoLevel);
               bucketsOut[2] = bucketMap.get(turnHash);
               bucketsOut[1] = bucketMap.get(turnHash / 100L);
               bucketsOut[3] = bucketMap.get(AnalysisPanel.is5Card() ? OmahaHandNormalizer.normalizeMulti5c(cardInts, sortedBuffer, riverLen, suitBuffer, isoLevel) : OmahaHandNormalizer.normalizeMulti(cardInts, sortedBuffer, 9, suitBuffer, isoLevel));
            }
         }

         reachProbOut[0] = computeReachProbabilityByNodes(pathNodeIds, pathActionIdxs, baseReachProb, bucketsOut);
         return bucketsOut[stage];
      }
   }

   private static int computeHoldemBucketAndReachProb(int[] actionPath, double[] reachProbOut, double baseReachProb, GameState state, card[] boardCards, card[] scratchCards, int[] bucketsOut, collections.LongIntHashMap bucketMap, int[] textureIndices) {
      int stage = state.gameStage;
      if (gameState.gameStage == 0) {
         bucketsOut = computeStreetBucketsFromTexture(boardCards, textureIndices);
      } else {
         long handHash = (new CardCombinations(boardCards)).computeHash();
         if (stage == 1) {
            bucketsOut[1] = bucketMap.get(handHash);
         } else if (stage == 2) {
            bucketsOut[1] = bucketMap.get(handHash / 100L);
            bucketsOut[2] = bucketMap.get(handHash);
         } else {
            bucketsOut[1] = bucketMap.get(handHash / 100L / 100L);
            bucketsOut[2] = bucketMap.get(handHash / 100L);
            bucketsOut[3] = bucketMap.get(handHash);
         }
      }

      reachProbOut[0] = computeReachProbabilityByPath(actionPath, gameState.gameStage == 0 ? 1.0D : baseReachProb, state.firstPlayerToAct, bucketsOut);
      return bucketsOut[stage];
   }

   private static synchronized BucketGenerator buildBucketGeneratorOmaha(GameStateRefreshTask refreshTask, GameState state, ArrayList boardList) {
      int[] rankCounts;
      int stage;
      double[] filterWeights;
      int numActions;
      if (EquitySortComparator.d != null && EquitySortComparator.d.countBuckets(state.gameStage) > 0) {
         boardList = boardList;
         state = state;
         refreshTask = refreshTask;
         numActions = getAvailableActions(state).length;
         int[] equityHistogram = new int[4];
         int numHoleCards5 = AnalysisPanel.is5Card() ? 5 : 4;
         int[] holeIndices = new int[numHoleCards5];
         rankCounts = new int[13];
         double[] handWeights = OmahaHandRange.getDefaultRange().weights;
         collections.LongIntHashMap bucketMap = null;
         if (gameState.gameStage > 0) {
            bucketMap = postflopBucketMaps[state.firstPlayerToAct];
            handWeights = playerStrategyWeights[state.firstPlayerToAct];
         }

         int nodeId = getNodeId(state);
         double weightedEquitySum = 0.0D;
         int totalCardCount;
         if ((stage = state.gameStage) == 0) {
            totalCardCount = numHoleCards5;
         } else if (stage == 1) {
            totalCardCount = numHoleCards5 + 3;
         } else if (stage == 2) {
            totalCardCount = numHoleCards5 + 4;
         } else {
            totalCardCount = numHoleCards5 + 5;
         }

         int totalCardCountAlias;
         if (gameState.gameStage == 0) {
            totalCardCountAlias = numHoleCards5;
         } else if (gameState.gameStage == 1) {
            totalCardCountAlias = numHoleCards5 + 3;
         } else if (gameState.gameStage == 2) {
            totalCardCountAlias = numHoleCards5 + 4;
         } else {
            totalCardCountAlias = numHoleCards5 + 5;
         }

         card[] allCards = new card[totalCardCount];
         int[] evalScratch = new int[10];
         int[] cardIndices = new int[totalCardCount];

         int initIdx;
         for(initIdx = 0; initIdx < cardIndices.length; ++initIdx) {
            cardIndices[initIdx] = -1;
         }

         for(initIdx = 0; initIdx < numHoleCards5; ++initIdx) {
            allCards[initIdx] = new card(1, 1);
         }

         card[] boardCards = new card[totalCardCount - numHoleCards5];

         for(int boardIdx = 0; boardIdx < totalCardCount - numHoleCards5; ++boardIdx) {
            allCards[boardIdx + numHoleCards5] = solver.card.parseCard((String)boardList.get(boardIdx));
            boardCards[boardIdx] = solver.card.parseCard((String)boardList.get(boardIdx));
            cardIndices[boardIdx + numHoleCards5] = allCards[boardIdx + numHoleCards5].getFullDeckIndex52();
         }

         int[] textureIndices = new int[3];
         if (gameState.gameStage == 0 && boardCards.length > 0) {
            card[] optimizedSuitCards = CardCombinations.optimizeSuits(boardCards);
            textureIndices[0] = boardCards.length > 0 ? flopTexture.get(CardCombinations.getCardArrayNumValue(optimizedSuitCards, 3)) : 0;
            textureIndices[1] = boardCards.length > 3 ? turnTexture.get(CardCombinations.getCardArrayNumValue(optimizedSuitCards, 4)) : 0;
            textureIndices[2] = boardCards.length > 4 ? riverTexture.get(CardCombinations.getCardArrayNumValue(optimizedSuitCards, 5)) : 0;
         }

         filterWeights = null;
         if (EquitySortComparator.activeFilter != null) {
            System.lineSeparator();
            filterWeights = EquitySortComparator.activeFilter.weights;
         }

         double[] actionEquities = new double[numActions];
         double[] strategyBuffer = new double[numActions];
         double[] actionEquityTotals = new double[numActions];
         int[] currentActions = solver.MainTabbedPane.getActionPathIds();
         double totalWeight = 0.0D;
         ViewSettingsManager viewSettings = EquitySortComparator.d;
         int[] filteredActionIds;
         int[] filteredActionPositions = new int[(filteredActionIds = new int[currentActions.length]).length];
         int numFilteredActions = collectPlayerActionIndices(filteredActionIds, filteredActionPositions, currentActions, state.firstPlayerToAct);
         filteredActionIds = Arrays.copyOf(filteredActionIds, numFilteredActions);
         viewSettings.resetFilterStats(numActions);
         int numHoleCards3 = AnalysisPanel.is5Card() ? 5 : 4;
         int comboCount3 = AnalysisPanel.is5Card() ? 2598960 : 270725;

         label327:
         for(int comboIdx = 0; comboIdx < comboCount3; ++comboIdx) {
            boolean filteredOut = false;
            if (handWeights[comboIdx] > 1.0E-7D) {
               if (filterWeights != null && filterWeights[comboIdx] <= 1.0E-7D) {
                  filteredOut = true;
               }

               if (refreshTask != null && refreshTask.cancelled) {
                  return null;
               }

               int comboEncoded = AnalysisPanel.is5Card() ? OmahaHandRange.indexToComboEncoding5c[comboIdx] : OmahaHandRange.indexToComboEncoding4c[comboIdx];

               int handBucketIndex;
               for(int holeIdx = 0; holeIdx < numHoleCards3; ++holeIdx) {
                  cardIndices[holeIdx] = comboEncoded % 52;

                  for(handBucketIndex = totalCardCountAlias; handBucketIndex < totalCardCount; ++handBucketIndex) {
                     if (cardIndices[holeIdx] == cardIndices[handBucketIndex]) {
                        continue label327;
                     }
                  }

                  comboEncoded /= 52;
               }

               HandFilterQuery filterQuery = viewSettings.findMatchingFilter(HandFilterParser.m, comboIdx, boardCards);
               if ((handBucketIndex = computeOmahaBucketAndReachProb(filteredActionIds, filteredActionPositions, actionEquities, handWeights[comboIdx], state, cardIndices, allCards, textureIndices, bucketMap, stage, evalScratch, rankCounts, holeIndices, equityHistogram)) < 0) {
                  System.lineSeparator();
               } else {
                  double handWeight = actionEquities[0];
                  getNormalizedStrategy(nodeId, handBucketIndex, actionEquities);
                  if (!filteredOut) {
                     filterQuery.aggregateEv += handWeight;
                     weightedEquitySum += handWeight;

                     for(int actionIdx = 0; actionIdx < numActions; ++actionIdx) {
                        double actionContribution;
                        if ((actionContribution = handWeight * actionEquities[actionIdx]) > 0.0D) {
                           filterQuery.addFrequency(actionIdx, actionContribution);
                           actionEquityTotals[actionIdx] += actionContribution;
                        }

                        if (readAverageStrategy(nodeId, handBucketIndex, strategyBuffer)) {
                           double adjustedContribution = actionContribution + 1.0E-10D;
                           filterQuery.addWeightedEv(actionIdx, strategyBuffer[actionIdx] * adjustedContribution);
                           filterQuery.addEvWeight(actionIdx, adjustedContribution);
                        }
                     }
                  }

                  totalWeight += handWeight;
               }
            }
         }

         solver.MainTabbedPane.updateFilterPercent(weightedEquitySum / totalWeight);
         return buildFilteredBucketGenerator(refreshTask, viewSettings, actionEquityTotals);
      } else {
         int[] currentActions = solver.MainTabbedPane.getActionPathIds();
         int isoLvl = isoLevel;
         int actionCount = getAvailableActions(state).length;
         int numHoleCards = AnalysisPanel.is5Card() ? 5 : 4;
         int comboCount = AnalysisPanel.is5Card() ? 2598960 : 270725;
         rankCounts = new int[numHoleCards];
         int[] suitNormScratch = new int[numHoleCards];
         int[] rankCountBuffer = new int[13];
         HandStatisticCollection.resetIndex();
         HandStatisticCollection[] perActionStats = new HandStatisticCollection[actionCount];

         for(stage = 0; stage < actionCount; ++stage) {
            perActionStats[stage] = new HandStatisticCollection();
         }

         double[] handWeights = OmahaHandRange.getDefaultRange().weights;
         collections.LongIntHashMap bucketMap = null;
         if (gameState.gameStage > 0) {
            bucketMap = postflopBucketMaps[state.firstPlayerToAct];
            handWeights = playerStrategyWeights[state.firstPlayerToAct];
         }

         equityCachePrimary.clear();
         equityCacheSecondary.clear();
         int nodeId = getNodeId(state);
         double filteredWeightTotal = 0.0D;
         int gameStageArg;
         int totalCardsCurrent;
         if ((gameStageArg = state.gameStage) == 0) {
            totalCardsCurrent = numHoleCards;
         } else if (gameStageArg == 1) {
            totalCardsCurrent = numHoleCards + 3;
         } else if (gameStageArg == 2) {
            totalCardsCurrent = numHoleCards + 4;
         } else {
            totalCardsCurrent = numHoleCards + 5;
         }

         int totalCardsGameState;
         if (gameState.gameStage == 0) {
            totalCardsGameState = numHoleCards;
         } else if (gameState.gameStage == 1) {
            totalCardsGameState = numHoleCards + 3;
         } else if (gameState.gameStage == 2) {
            totalCardsGameState = numHoleCards + 4;
         } else {
            totalCardsGameState = numHoleCards + 5;
         }

         card[] comboCards = new card[totalCardsCurrent];
         int[] suitMap = new int[10];
         int[] cardIndices52 = new int[totalCardsCurrent];

         int initIdx;
         for(initIdx = 0; initIdx < cardIndices52.length; ++initIdx) {
            cardIndices52[initIdx] = -1;
         }

         for(initIdx = 0; initIdx < numHoleCards; ++initIdx) {
            comboCards[initIdx] = new card(1, 1);
         }

         card[] boardCards = new card[totalCardsCurrent - numHoleCards];

         for(int boardIdx = 0; boardIdx < totalCardsCurrent - numHoleCards; ++boardIdx) {
            comboCards[boardIdx + numHoleCards] = solver.card.parseCard((String)boardList.get(boardIdx));
            boardCards[boardIdx] = solver.card.parseCard((String)boardList.get(boardIdx));
            cardIndices52[boardIdx + numHoleCards] = comboCards[boardIdx + numHoleCards].getFullDeckIndex52();
         }

         int[] textureBuckets = new int[3];
         if (gameState.gameStage == 0 && boardCards.length > 0) {
            boardCards = CardCombinations.optimizeSuits(boardCards);
            textureBuckets[0] = comboCards.length > 4 ? flopTexture.get(CardCombinations.getCardArrayNumValue(boardCards, 3)) : 0;
            textureBuckets[1] = comboCards.length > 7 ? turnTexture.get(CardCombinations.getCardArrayNumValue(boardCards, 4)) : 0;
            textureBuckets[2] = comboCards.length > 8 ? riverTexture.get(CardCombinations.getCardArrayNumValue(boardCards, 5)) : 0;
         }

         filterWeights = null;
         if (EquitySortComparator.activeFilter != null) {
            System.lineSeparator();
            filterWeights = EquitySortComparator.activeFilter.weights;
         }

         double[] strategyBuffer = new double[actionCount];
         double[] strategySum = new double[actionCount];
         double totalWeight = 0.0D;

         int comboIdx;
         int comboPacked;
         int holeIdx;
         int boardIdx;
         int actionOrHandEvalRank;
         double handEquity;
         double handWeight;
         label449:
         for(comboIdx = 0; comboIdx < comboCount; ++comboIdx) {
            boolean skipInFilter = false;
            if (handWeights[comboIdx] > 1.0E-7D) {
               if (filterWeights != null && filterWeights[comboIdx] <= 1.0E-7D) {
                  skipInFilter = true;
               }

               if (refreshTask != null && refreshTask.cancelled) {
                  return null;
               }

               comboPacked = AnalysisPanel.is5Card() ? OmahaHandRange.indexToComboEncoding5c[comboIdx] : OmahaHandRange.indexToComboEncoding4c[comboIdx];

               for(holeIdx = 0; holeIdx < numHoleCards; ++holeIdx) {
                  cardIndices52[holeIdx] = comboPacked % 52;

                  for(boardIdx = totalCardsGameState; boardIdx < totalCardsCurrent; ++boardIdx) {
                     if (cardIndices52[holeIdx] == cardIndices52[boardIdx]) {
                        continue label449;
                     }
                  }

                  comboPacked /= 52;
               }

               if (gameState.gameStage == 0) {
                  for(actionOrHandEvalRank = 0; actionOrHandEvalRank < numHoleCards; ++actionOrHandEvalRank) {
                     comboCards[actionOrHandEvalRank].rank = cardIndices52[actionOrHandEvalRank] % 13 + 2;
                     if (comboCards[actionOrHandEvalRank].rank == 14) {
                        comboCards[actionOrHandEvalRank].rank = 1;
                     }

                     comboCards[actionOrHandEvalRank].suit = cardIndices52[actionOrHandEvalRank] / 13;
                  }

                  if (gameStageArg >= 3) {
                     actionOrHandEvalRank = AnalysisPanel.is5Card() ? handeval.PloHandEvaluator.evaluatePlo5Hi(cardIndices52) : handeval.PloHandEvaluator.evaluatePlo4Hi(cardIndices52);
                     if (AnalysisPanel.gameType == 2) {
                        actionOrHandEvalRank |= handeval.PloHandEvaluator.evaluateLowHand(cardIndices52, rankCountBuffer) << 16;
                     }
                  } else {
                     actionOrHandEvalRank = 0;
                  }

                  rankCounts = computeOmahaBucketsFromInts(rankCounts, textureBuckets, cardIndices52, suitMap, suitNormScratch, actionOrHandEvalRank, comboCards.length);
               } else if (gameStageArg == 1) {
                  int flopLen = numHoleCards + 3;
                  rankCounts[1] = bucketMap.get(AnalysisPanel.is5Card() ? OmahaHandNormalizer.normalizeMulti5c(cardIndices52, suitMap, flopLen, suitNormScratch, isoLevel) : OmahaHandNormalizer.normalizeMulti(cardIndices52, suitMap, flopLen, suitNormScratch, isoLevel));
               } else {
                  long handKey;
                  int turnLen = numHoleCards + 4;
                  int riverLen = numHoleCards + 5;
                  if (gameStageArg == 2) {
                     handKey = AnalysisPanel.is5Card() ? OmahaHandNormalizer.normalizeMulti5c(cardIndices52, suitMap, turnLen, suitNormScratch, isoLevel) : OmahaHandNormalizer.normalizeMulti(cardIndices52, suitMap, turnLen, suitNormScratch, isoLevel);
                     rankCounts[2] = bucketMap.get(handKey);
                     rankCounts[1] = bucketMap.get(handKey / 100L);
                  } else if (gameStageArg == 3) {
                     handKey = AnalysisPanel.is5Card() ? OmahaHandNormalizer.normalizeMulti5c(cardIndices52, suitMap, turnLen, suitNormScratch, isoLevel) : OmahaHandNormalizer.normalizeMulti(cardIndices52, suitMap, turnLen, suitNormScratch, isoLevel);
                     rankCounts[2] = bucketMap.get(handKey);
                     rankCounts[1] = bucketMap.get(handKey / 100L);
                     rankCounts[3] = bucketMap.get(AnalysisPanel.is5Card() ? OmahaHandNormalizer.normalizeMulti5c(cardIndices52, suitMap, riverLen, suitNormScratch, isoLevel) : OmahaHandNormalizer.normalizeMulti(cardIndices52, suitMap, riverLen, suitNormScratch, isoLevel));
                  }
               }

               handEquity = computeReachProbabilityByPath(currentActions, 1.0D, state.firstPlayerToAct, rankCounts);
               handWeight = GameSettings.b ? 1.0D : handWeights[comboIdx];
               if (!skipInFilter) {
                  filteredWeightTotal += handEquity * handWeight;
                  long handKey = AnalysisPanel.is5Card() ? OmahaHandNormalizer.normalize5c(cardIndices52, suitMap, totalCardsCurrent, suitNormScratch, isoLvl) : OmahaHandNormalizer.normalize(cardIndices52, suitMap, totalCardsCurrent, suitNormScratch, isoLvl);
                  equityCacheSecondary.adjustOrPutValue(handKey, handEquity * handWeight, handEquity * handWeight);
               }

               totalWeight += handEquity * handWeight;
            }
         }

         System.lineSeparator();
         troveCollection.clear();

         label421:
         for(comboIdx = 0; comboIdx < comboCount; ++comboIdx) {
            if (handWeights[comboIdx] > 1.0E-7D && (filterWeights == null || filterWeights[comboIdx] > 1.0E-7D)) {
               if (refreshTask != null && refreshTask.cancelled) {
                  return null;
               }

               comboPacked = AnalysisPanel.is5Card() ? OmahaHandRange.indexToComboEncoding5c[comboIdx] : OmahaHandRange.indexToComboEncoding4c[comboIdx];

               for(holeIdx = 0; holeIdx < numHoleCards; ++holeIdx) {
                  cardIndices52[holeIdx] = comboPacked % 52;

                  for(boardIdx = totalCardsGameState; boardIdx < totalCardsCurrent; ++boardIdx) {
                     if (cardIndices52[holeIdx] == cardIndices52[boardIdx]) {
                        continue label421;
                     }
                  }

                  comboPacked /= 52;
               }

               long handKey = AnalysisPanel.is5Card() ? OmahaHandNormalizer.normalize5c(cardIndices52, suitMap, totalCardsCurrent, suitNormScratch, isoLvl) : OmahaHandNormalizer.normalize(cardIndices52, suitMap, totalCardsCurrent, suitNormScratch, isoLvl);
               if (troveCollection.add(handKey) && (handWeight = equityCacheSecondary.get(handKey)) / filteredWeightTotal >= GameSettings.j) {
                  int j;
                  int postflopBucketIndex;
                  if (gameState.gameStage == 0) {
                     for(j = 0; j < numHoleCards; ++j) {
                        comboCards[j].rank = cardIndices52[j] % 13 + 2;
                        if (comboCards[j].rank == 14) {
                           comboCards[j].rank = 1;
                        }

                        comboCards[j].suit = cardIndices52[j] / 13;
                     }

                     if (gameStageArg >= 3) {
                        j = AnalysisPanel.is5Card() ? handeval.PloHandEvaluator.evaluatePlo5Hi(cardIndices52) : handeval.PloHandEvaluator.evaluatePlo4Hi(cardIndices52);
                        if (AnalysisPanel.gameType == 2) {
                           j |= handeval.PloHandEvaluator.evaluateLowHand(cardIndices52, rankCountBuffer) << 16;
                        }
                     } else {
                        j = 0;
                     }

                     postflopBucketIndex = computeOmahaBucketsFromInts(rankCounts, textureBuckets, cardIndices52, suitMap, suitNormScratch, j, comboCards.length)[gameStageArg];
                  } else {
                     postflopBucketIndex = bucketMap.get(AnalysisPanel.is5Card() ? OmahaHandNormalizer.normalizeMulti5c(cardIndices52, suitMap, totalCardsCurrent, suitNormScratch, isoLevel) : OmahaHandNormalizer.normalizeMulti(cardIndices52, suitMap, totalCardsCurrent, suitNormScratch, isoLevel));
                  }

                  if (postflopBucketIndex >= 0) {
                     getNormalizedStrategy(nodeId, postflopBucketIndex, strategyBuffer);

                     for(j = 0; j < actionCount; ++j) {
                        strategySum[j] += strategyBuffer[j] * handWeight;
                     }

                     if (gameStageArg > 0) {
                        long handKeyDecode = handKey;

                        for(int holeDecodeIdx = numHoleCards - 1; holeDecodeIdx >= 0; --holeDecodeIdx) {
                           numActions = (int)(handKeyDecode % 100L) - 1;
                           comboCards[holeDecodeIdx].rank = numActions % 13 + 2;
                           if (comboCards[holeDecodeIdx].rank == 14) {
                              comboCards[holeDecodeIdx].rank = 1;
                           }

                           comboCards[holeDecodeIdx].suit = numActions / 13;
                           handKeyDecode /= 100L;
                        }
                     }

                     for(j = 0; j < actionCount; ++j) {
                        if (!GameSettings.l || strategyBuffer[j] >= GameSettings.k) {
                           perActionStats[j].recordStat(handWeight, comboCards, (int)(10000.0D * strategyBuffer[j]), postflopBucketIndex, getEV(nodeId, postflopBucketIndex, j));
                        }
                     }
                  }
               }
            }
         }

         System.lineSeparator();
         solver.MainTabbedPane.updateFilterPercent(filteredWeightTotal / totalWeight);
         double strategySumTotal = 0.0D;
         double[] strategySumAlias = strategySum;

         for(actionOrHandEvalRank = 0; actionOrHandEvalRank < actionCount; ++actionOrHandEvalRank) {
            handEquity = strategySumAlias[actionOrHandEvalRank];
            strategySumTotal += handEquity;
         }

         Object[][] perActionRows = new Object[actionCount][];
         String[] perActionPercent = new String[actionCount];

         for(actionOrHandEvalRank = 0; actionOrHandEvalRank < actionCount; ++actionOrHandEvalRank) {
            if (refreshTask != null && refreshTask.cancelled) {
               return null;
            }

            perActionPercent[actionOrHandEvalRank] = formatAsPercent(strategySum[actionOrHandEvalRank] / strategySumTotal);
            perActionRows[actionOrHandEvalRank] = new Object[perActionStats[actionOrHandEvalRank].size()];
            perActionStats[actionOrHandEvalRank].sort();
            int rowIdx = 0;

            PlayerHandStatistic stat;
            for(Iterator statIter = perActionStats[actionOrHandEvalRank].iterator(); statIter.hasNext(); perActionRows[actionOrHandEvalRank][rowIdx++] = stat) {
               stat = (PlayerHandStatistic)statIter.next();
            }
         }

         System.lineSeparator();
         return new BucketGenerator(perActionPercent, perActionRows);
      }
   }

   public static String formatAsPercent(double value) {
      if (value < 0.0D) {
         return solver.HashUtil.decodeBy28(new char[0]);
      } else {
         String formatted;
         if ((formatted = solver.HashUtil.decodeBy29(new char[0]) + Math.round(value * 1000.0D)).length() == 1) {
            formatted = "0" + formatted;
         }

         return formatted.substring(0, formatted.length() - 1) + "." + formatted.charAt(formatted.length() - 1) + "%";
      }
   }

   public static String formatWithPrecision(double value, int precision) {
      return String.format("%." + precision + "g%n", value);
   }

   public static int getNodeId(GameState state) {
      return (Integer)gameStateToNodeId.get(state);
   }

   public static double[] getStrategyForHand(GameState state, int handIndex) {
      return getStrategyArrayForNode((Integer)gameStateToNodeId.get(state), handIndex);
   }

   private static double getStrategyFrequency(int nodeId, int handIndex, int actionIndex) {
      if (avg[nodeId] == null) {
         return getRawStrategyValue(nodeId, handIndex, actionIndex);
      } else {
         double[] avgStrategy = avg[nodeId];
         nodeId = nodeActionCounts[nodeId];
         if (handIndex < 0 || actionIndex < 0) {
            return 1.0D / (double)nodeId;
         }
         handIndex *= nodeId;
         double totalWeight = 0.0D;

         for(int i = 0; i < nodeId; ++i) {
            if (handIndex + i >= 0 && handIndex + i < avgStrategy.length) {
               totalWeight += avgStrategy[handIndex + i];
            }
         }

         return totalWeight > 0.0D && handIndex + actionIndex >= 0 && handIndex + actionIndex < avgStrategy.length ? avgStrategy[handIndex + actionIndex] / totalWeight : 1.0D / (double)nodeId;
      }
   }

   private static double[] getStrategyArrayForNode(int nodeId, int handIndex) {
      if (avg[nodeId] == null) {
         return getStrategyFromCfrTables(nodeId, handIndex);
      } else {
         int numActions;
         double[] result = new double[numActions = nodeActionCounts[nodeId]];
         double totalWeight;
         if ((totalWeight = copyStrategyToArray(nodeId, handIndex, result)) <= 0.0D) {
            for(nodeId = 0; nodeId < numActions; ++nodeId) {
               result[nodeId] = 1.0D / (double)numActions;
            }

            return result;
         } else {
            for(nodeId = 0; nodeId < numActions; ++nodeId) {
               result[nodeId] /= totalWeight;
            }

            return result;
         }
      }
   }

   public static double getEV(int nodeId, int handIndex, int actionIndex) {
      if (handRangeReader != null) {
         return handRangeReader.getEvDelta(nodeId, handIndex, nodeActionCounts[nodeId], actionIndex);
      } else if (hasEV[UnsafeMemoryStorage.getPlayerStreetIndex((long)nodeId)]) {
         double[] cfrTable = cfrTables[UnsafeMemoryStorage.getPlayerStreetIndex((long)nodeId)][handIndex];
         int numActions = nodeActionCounts[nodeId];
         nodeId = UnsafeMemoryStorage.getNodeOffset((long)nodeId);
         double regret = cfrTable[nodeId + actionIndex];
         double iterations;
         if ((iterations = cfrTable[nodeId + numActions]) == 0.0D) {
            return Double.NEGATIVE_INFINITY;
         } else {
            double baseline = cfrTable[nodeId + numActions + 1];
            return (regret + baseline) / iterations;
         }
      } else {
         return Double.NEGATIVE_INFINITY;
      }
   }

   public static boolean readAverageStrategy(int nodeId, int handIndex, double[] result) {
      if (handRangeReader != null) {
         return handRangeReader.readEvDeltas(nodeId, handIndex, result);
      } else if (!hasEV[UnsafeMemoryStorage.getPlayerStreetIndex((long)nodeId)]) {
         return false;
      } else {
         double[] cfrTable = cfrTables[UnsafeMemoryStorage.getPlayerStreetIndex((long)nodeId)][handIndex];
         int numActions = nodeActionCounts[nodeId];
         nodeId = UnsafeMemoryStorage.getNodeOffset((long)nodeId);
         double iterations;
         if ((iterations = cfrTable[nodeId + numActions]) < 1.0E-11D) {
            return false;
         } else {
            double baseline = cfrTable[nodeId + numActions + 1];

            for(int actionIdx = 0; actionIdx < numActions; ++actionIdx) {
               result[actionIdx] = (cfrTable[nodeId + actionIdx] + baseline) / iterations;
            }

            return true;
         }
      }
   }

   public static double[] getNormalizedStrategy(int nodeId, int handIndex, double[] result) {
      if (avg[nodeId] == null) {
         copyStrategyFromCfrTables(nodeId, handIndex, result);
         return result;
      } else {
         double total = copyStrategyToArray(nodeId, handIndex, result);
         nodeId = nodeActionCounts[nodeId];
         if (total <= 0.0D) {
            double uniform = 1.0D / (double)nodeId;

            for(handIndex = 0; handIndex < nodeId; ++handIndex) {
               result[handIndex] = uniform;
            }

            return result;
         } else {
            for(int i = 0; i < nodeId; ++i) {
               result[i] /= total;
            }

            return result;
         }
      }
   }

   private static double[] getStrategyFromCfrTables(int nodeId, int handIndex) {
      double[] result = new double[nodeActionCounts[nodeId]];
      copyStrategyFromCfrTables(nodeId, handIndex, result);
      return result;
   }

   private static double getRawStrategyValue(int nodeId, int handIndex, int actionIndex) {
      if (handRangeReader != null) {
         return handRangeReader.getActionProbability(nodeId, handIndex, nodeActionCounts[nodeId], actionIndex);
      } else {
         double[] cfrTable = cfrTables[UnsafeMemoryStorage.getPlayerStreetIndex((long)nodeId)][handIndex];
         int numActions = nodeActionCounts[nodeId];
         int offset = UnsafeMemoryStorage.getNodeOffset((long)nodeId);
         handIndex *= numActions;
         double[] adjustments;
         if ((adjustments = UnsafeMemoryStorage.getAdjustments(nodeId)) != null && adjustments[handIndex + actionIndex] >= 0.0D) {
            return adjustments[handIndex + actionIndex];
         } else {
            double totalRegret = 0.0D;
            double remainingWeight = 1.0D;
            int activeActions = numActions;
            synchronized(cfrTable) {
               for(int i = 0; i < numActions; ++i) {
                  if (adjustments != null && adjustments[handIndex + i] >= 0.0D) {
                     --activeActions;
                     remainingWeight -= adjustments[handIndex + i];
                  } else if (cfrTable[offset + i] > 0.0D) {
                     totalRegret += cfrTable[offset + i];
                  }
               }

               if (totalRegret > 0.0D) {
                  if (cfrTable[offset + actionIndex] > 0.0D) {
                     return cfrTable[offset + actionIndex] * remainingWeight / totalRegret;
                  } else {
                     return 0.0D;
                  }
               } else {
                  return remainingWeight / (double)activeActions;
               }
            }
         }
      }
   }

   public static final void copyStrategyFromCfrTables(int nodeId, int handIndex, double[] result) {
      if (handRangeReader != null) {
         handRangeReader.fillActionProbabilities(nodeId, handIndex, result);
      } else {
         int numActions = result.length;
         int offset = UnsafeMemoryStorage.getNodeOffset((long)nodeId);
         double totalRegret = 0.0D;
         double remainingWeight = 1.0D;
         double[] cfrTable = cfrTables[UnsafeMemoryStorage.getPlayerStreetIndex((long)nodeId)][handIndex];
         double[] adjustments = UnsafeMemoryStorage.getAdjustments(nodeId);
         int activeActions = numActions;
         handIndex *= numActions;

         for(int i = 0; i < numActions; ++i) {
            if (adjustments != null && adjustments[handIndex + i] >= 0.0D) {
               remainingWeight -= adjustments[handIndex + i];
               --activeActions;
               result[i] = Double.NEGATIVE_INFINITY;
            } else {
               result[i] = cfrTable[offset + i];
            }

            if (result[i] > 0.0D) {
               totalRegret += result[i];
            }
         }

         double scale;
         if (totalRegret > 0.0D) {
            scale = remainingWeight / totalRegret;

            for(offset = 0; offset < numActions; ++offset) {
               if (result[offset] > 0.0D) {
                  result[offset] *= scale;
               } else if (result[offset] != Double.NEGATIVE_INFINITY) {
                  result[offset] = 0.0D;
               } else {
                  result[offset] = adjustments[handIndex + offset];
               }
            }

         } else {
            scale = remainingWeight / (double)activeActions;

            for(offset = 0; offset < numActions; ++offset) {
               if (result[offset] != Double.NEGATIVE_INFINITY) {
                  result[offset] = scale;
               } else {
                  result[offset] = adjustments[handIndex + offset];
               }
            }

         }
      }
   }

   private static final double copyStrategyToArray(int nodeId, int handIndex, double[] result) {
      double[] avgStrategy = avg[nodeId];
      double total = 0.0D;

      for(int i = 0; i < result.length; ++i) {
         result[i] = avgStrategy[handIndex * result.length + i];
         total += result[i];
      }

      return total;
   }

   public static File getSavedRunsDirectory() {
      if (savedRunsDirectory == null) {
         savedRunsDirectory = new util.AppFile("savedRuns");
      }

      if (!savedRunsDirectory.exists()) {
         savedRunsDirectory.mkdirs();
      }

      return savedRunsDirectory;
   }

   public static void setSavedRunsDirectory(File directory) {
      savedRunsDirectory = directory;
   }

   public static Thread saveSimulation(File file, int streetMask) {
      solver.MainTabbedPane.saveButton.setIcon(solver.MainTabbedPane.runIcon);
      boolean wasPaused = solver.SolverRunner.stopCalculation();
      Thread saverThread;
      (saverThread = new Thread(new CalcSaver(file, streetMask, wasPaused))).start();
      return saverThread;
   }

   public static void setSavedFile(File file) {
      savedFile = file;
      if (!solver.MainTabbedPane.testMode) {
         if (file == null) {
            solver.MainTabbedPane.k.setTitle("Squid v2 [New run]");
            return;
         }

         solver.MainTabbedPane.k.setTitle("Squid v2 [" + file.getAbsolutePath() + "]");
      }

   }

   public static void restoreAdjustments(double[][] adjustmentTable) {
      try {
         for(int nodeId = 0; nodeId < adjustmentTable.length; ++nodeId) {
            if (adjustmentTable[nodeId] != null) {
               int numActions = nodeActionCounts[nodeId];
               double[] adjustments = adjustmentTable[nodeId];

               for(int slot = 0; slot < adjustments.length; ++slot) {
                  boolean isLocked;
                  label33: {
                     int handIdx = slot / numActions;
                     int actionIdx = slot % numActions;
                     if (cfrTables != null) {
                        double[] cfrRow = cfrTables[UnsafeMemoryStorage.getPlayerStreetIndex((long)nodeId)][handIdx];
                        handIdx = UnsafeMemoryStorage.getNodeOffset((long)nodeId);
                        if (cfrRow[handIdx + actionIdx] == Double.NEGATIVE_INFINITY) {
                           isLocked = true;
                           break label33;
                        }
                     }

                     isLocked = false;
                  }

                  if (!isLocked) {
                     adjustments[slot] = -1.0D;
                  }
               }

               UnsafeMemoryStorage.setAdjustments(nodeId, adjustments);
            }
         }

      } catch (Exception ignored) {
      }
   }

   public static Thread loadSimulation(File file, int streetMask) {
      if (!file.exists()) {
         return null;
      } else {
         if (solver.MainTabbedPane.loadButton != null) {
            solver.MainTabbedPane.loadButton.setIcon(solver.MainTabbedPane.runIcon);
         }

         Thread loaderThread;
         (loaderThread = new Thread(new CalcReader(file, streetMask))).start();
         return loaderThread;
      }
   }
}
