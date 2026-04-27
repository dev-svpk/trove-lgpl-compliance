package solver;

import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipFile;

public final class Equity {
   private static volatile long equityCalculationTimestamp;

   static {
      new Random();
   }

   public static collections.LongIntHashMap convertToLongIntHashMap(gnu.trove.map.TLongIntMap sourceMap) {
      if (sourceMap == null) {
         return null;
      } else {
         collections.LongIntHashMap resultMap = new collections.LongIntHashMap(sourceMap.size());
         gnu.trove.iterator.TLongIntIterator iterator = sourceMap.iterator();

         while(iterator.hasNext()) {
            iterator.advance();
            resultMap.put(iterator.key(), iterator.value());
         }

         return resultMap;
      }
   }

   public static final int setBit(int bitset, int bitIndex) {
      return bitset | 1 << bitIndex;
   }

   public static final boolean isBitSet(int bitset, int bitIndex) {
      return (bitset & 1 << bitIndex) != 0;
   }

   public static final long setBitLong(long bitset, int bitIndex) {
      return bitset | 1L << bitIndex;
   }

   public static final boolean isBitSetLong(long bitset, int bitIndex) {
      return (bitset & 1L << bitIndex) != 0L;
   }

   public static void runEquitySimulation(EquityChartPanel chartPanel, List handRanges, card[] boardCards) {
      int playerCount = handRanges.size();
      ArrayList handIndicesList = new ArrayList();
      ArrayList handCardBytesList = new ArrayList();
      ArrayList handFreqArraysList = new ArrayList();
      ArrayList equitySumsList = new ArrayList();
      ArrayList countSumsList = new ArrayList();

      int holeCardCount;
      for(holeCardCount = 0; holeCardCount < handRanges.size(); ++holeCardCount) {
         int[][] handIndices;
         if ((handIndices = ((HandRange)handRanges.get(holeCardCount)).getCombos()).length == 0) {
            return;
         }

         handCardBytesList.add(handIndices);
         handIndicesList.add(((HandRange)handRanges.get(holeCardCount)).toByteArray());
         handFreqArraysList.add(((HandRange)handRanges.get(holeCardCount)).getNonZeroWeights());
         equitySumsList.add(new double[((int[][])handCardBytesList.get(holeCardCount)).length]);
         countSumsList.add(new double[((int[][])handCardBytesList.get(holeCardCount)).length]);
      }

      int[] boardSlots = new int[(holeCardCount = AnalysisPanel.getHoleCardCount()) + 5];
      long deadCardMask = 0L;
      int[] boardDeckIndices = new int[boardCards.length];

      int boardCardIdx;
      for(int boardIter = 0; boardIter < boardCards.length; ++boardIter) {
         boardDeckIndices[boardIter] = holeCardCount == 2 ? boardCards[boardIter].getFullDeckIndex() : boardCards[boardIter].getFullDeckIndex52();
         boardCardIdx = boardDeckIndices[boardIter];
         deadCardMask |= 1L << boardCardIdx;
         boardSlots[boardIter + holeCardCount] = boardDeckIndices[boardIter];
      }

      XorShiftRandomGenerator rng = new XorShiftRandomGenerator();
      int cardsToDeal = 5 - boardDeckIndices.length;
      int[][] playerHoleCards = new int[handRanges.size()][holeCardCount];
      int[] chosenHandIdx = new int[handRanges.size()];
      int[] loQualifierScratch = new int[11];
      int[] playerHiRanks = new int[handRanges.size()];
      int[] playerLoRanks = null;
      if (AnalysisPanel.gameType == 2) {
         playerLoRanks = new int[playerHiRanks.length];
      }

      for(int playerIdx = 0; playerIdx < handRanges.size(); ++playerIdx) {
         double[] handFreqs = (double[])handFreqArraysList.get(playerIdx);
         ArrayList dataPoints = new ArrayList();

         for(int handIdx = 0; handIdx < handFreqs.length; ++handIdx) {
            dataPoints.add(new EquityDataPoint(((int[][])handCardBytesList.get(playerIdx))[handIdx], 1.0D / (double)playerCount, handFreqs[handIdx]));
         }

         ((HandRange)handRanges.get(playerIdx)).equityData = dataPoints;
      }

      long reportIntervalMs = (reportIntervalMs = (long)(holeCardCount == 2 ? 500 : 1000)) * (long)handRanges.size();
      long maxReportIntervalMs = (long)(holeCardCount == 2 ? 1000 * handRanges.size() : 15000 * handRanges.size());
      equityCalculationTimestamp = System.currentTimeMillis();
      long baseDeadMask = deadCardMask;

      for(holeCardCount = 0; holeCardCount < 500000000 && !chartPanel.d; ++holeCardCount) {
         if (System.currentTimeMillis() - equityCalculationTimestamp > reportIntervalMs) {
            if ((reportIntervalMs += reportIntervalMs / 2L) > maxReportIntervalMs) {
               reportIntervalMs = maxReportIntervalMs;
            }

            int handIdxInner;
            double[] panelArray;
            for(int playerReport = 0; playerReport < handRanges.size(); ++playerReport) {
               double[] freqs = (double[])handFreqArraysList.get(playerReport);
               double[] equitySum = (double[])equitySumsList.get(playerReport);
               double[] countSum = (double[])countSumsList.get(playerReport);
               synchronized(((HandRange)handRanges.get(playerReport)).equityData) {
                  chartPanel.b[playerReport] = 0.0D;

                  for(handIdxInner = 0; handIdxInner < freqs.length; ++handIdxInner) {
                     EquityDataPoint dataPoint;
                     (dataPoint = (EquityDataPoint)((HandRange)handRanges.get(playerReport)).equityData.get(handIdxInner)).cards = ((int[][])handCardBytesList.get(playerReport))[handIdxInner];
                     if (countSum[handIdxInner] > 0.0D) {
                        double avgEquity = equitySum[handIdxInner] / countSum[handIdxInner];
                        dataPoint.equity = avgEquity;
                        dataPoint.weight = countSum[handIdxInner];
                     } else {
                        dataPoint.weight = countSum[handIdxInner];
                        dataPoint.equity = 1.0D / (double)playerCount;
                     }

                     panelArray = chartPanel.b;
                     panelArray[playerReport] += dataPoint.weight;
                     panelArray = chartPanel.c;
                     panelArray[playerReport] += dataPoint.equity * dataPoint.weight;
                  }

                  Collections.sort(((HandRange)handRanges.get(playerReport)).equityData);
               }
            }

            double totalWeight = 0.0D;
            double[] equityArr;
            handIdxInner = (equityArr = chartPanel.c).length;

            for(boardCardIdx = 0; boardCardIdx < handIdxInner; ++boardCardIdx) {
               double equityValue = equityArr[boardCardIdx];
               totalWeight += equityValue;
            }

            for(int normPlayer = 0; normPlayer < playerCount; ++normPlayer) {
               panelArray = chartPanel.c;
               panelArray[normPlayer] /= totalWeight;
            }

            equityCalculationTimestamp = Long.MAX_VALUE;
            (new Thread(() -> {
               chartPanel.k = false;
               equityCalculationTimestamp = chartPanel.recomputeCurves();
            })).start();
         }

         if (AnalysisPanel.gameType == 1 || AnalysisPanel.gameType == 4) {
            // Use simulatePlo5Round for 5-card PLO (gameType == 4), simulatePlo4Round for 4-card PLO (gameType == 1)
            if (AnalysisPanel.is5Card()) {
               simulatePlo5Round(chartPanel, equitySumsList, countSumsList, playerCount, baseDeadMask, boardSlots, playerHiRanks, handIndicesList, handFreqArraysList, rng, chosenHandIdx, playerHoleCards, cardsToDeal);
            } else {
               simulatePlo4Round(chartPanel, equitySumsList, countSumsList, playerCount, baseDeadMask, boardSlots, playerHiRanks, handIndicesList, handFreqArraysList, rng, chosenHandIdx, playerHoleCards, cardsToDeal);
            }
         } else if (AnalysisPanel.gameType == 2) {
            simulatePloHiLoRound(chartPanel, equitySumsList, countSumsList, playerCount, baseDeadMask, boardSlots, loQualifierScratch, playerHiRanks, playerLoRanks, handIndicesList, handFreqArraysList, rng, chosenHandIdx, playerHoleCards, cardsToDeal);
         } else {
            AnalysisPanel.isDebugMode();
            simulateHoldemRound(chartPanel, equitySumsList, countSumsList, playerCount, baseDeadMask, boardSlots, playerHiRanks, handIndicesList, handFreqArraysList, rng, chosenHandIdx, playerHoleCards, cardsToDeal);
         }
      }

   }

   private static void simulateHoldemRound(EquityChartPanel chartPanel, List equitySumsList, List countSumsList, int playerCount, long baseDeadMask, int[] boardSlots, int[] playerRanks, List handCardBytesList, List cumulativeFreqsList, XorShiftRandomGenerator rng, int[] chosenHandIdx, int[][] playerHoleCards, int cardsToDeal) {
      label76:
      while(!chartPanel.d) {
         long deadMask = baseDeadMask;

         int playerIter;
         int sampledHandIdx;
         int altBoardKey = 0;
         int holeBase;
         int[] holePair;
         int cardDeckIdx;
         int evaluatedRank;
         int alternateHandRank;
         boolean acesOnBoard;
         for(playerIter = 0; playerIter < playerCount; ++playerIter) {
            byte[] handCardBytes = (byte[])handCardBytesList.get(playerIter);
            double[] cumulativeFreqs = (double[])cumulativeFreqsList.get(playerIter);

            for(sampledHandIdx = rng.nextInt(cumulativeFreqs.length); cumulativeFreqs[sampledHandIdx] < 1.0D && rng.nextDouble() > cumulativeFreqs[sampledHandIdx]; sampledHandIdx = rng.nextInt(cumulativeFreqs.length)) {
            }

            holeBase = sampledHandIdx << 1;
            if (isBitSetLong(deadMask, handCardBytes[holeBase]) || isBitSetLong(deadMask, handCardBytes[holeBase + 1])) {
               continue label76;
            }

            chosenHandIdx[playerIter] = sampledHandIdx;
            (holePair = playerHoleCards[playerIter])[0] = handCardBytes[holeBase];
            holePair[1] = handCardBytes[holeBase + 1];
            cardDeckIdx = holePair[0];
            long holeMaskAccum = deadMask | 1L << cardDeckIdx;
            cardDeckIdx = holePair[1];
            deadMask = holeMaskAccum | 1L << cardDeckIdx;
         }

         int randomCard;
         int boardSlotPos;
         for(playerIter = cardsToDeal - 1; playerIter >= 0; --playerIter) {

        	if (AnalysisPanel.gameType == 0) {
	            for(randomCard = rng.nextInt(52); isBitSetLong(deadMask, randomCard); randomCard = rng.nextInt(52)) {
	            }
        	} else {
        		for(randomCard = 16 + rng.nextInt(36); isBitSetLong(deadMask, randomCard); randomCard = 16 + rng.nextInt(36)) {
	            }
        	}

            boardSlotPos = 2 + (5 - cardsToDeal) + playerIter;
            boardSlots[boardSlotPos] = randomCard;
            cardDeckIdx = boardSlots[boardSlotPos];
            deadMask |= 1L << cardDeckIdx;
         }

         randomCard = Integer.MIN_VALUE;
         boardSlotPos = 1;
         sampledHandIdx = handeval.tables.HandRankEvaluator.precomputeBoardKey(boardSlots);

         altBoardKey = 0;
         acesOnBoard = false;
         if (AnalysisPanel.gameType == 3) {
      	   for (int i = 2; i<7; i++) {
      		   if (boardSlots[i] > 47) {
      			   acesOnBoard = true;
      			   break;
      		   }
      	   }

      	   if (acesOnBoard) {
      		 altBoardKey = handeval.tables.HandRankEvaluator.precomputeBoardKeyA5swap(boardSlots);
      	   }
         }

         for(holeBase = 0; holeBase < playerCount; ++holeBase) {
            holePair = playerHoleCards[holeBase];
            //playerRanks[holeBase] = handeval.tables.HandRankEvaluator.evaluateWithHoleCards(sampledHandIdx, holePair[0], holePair[1]);
            evaluatedRank = handeval.tables.HandRankEvaluator.evaluateWithHoleCards(sampledHandIdx, holePair[0], holePair[1]);

            if (AnalysisPanel.gameType == 3) {
          	  if (acesOnBoard) {
          		  if ( (holePair[0] > 47) || (holePair[1] > 47)) {
          			  alternateHandRank = handeval.tables.HandRankEvaluator.evaluateWithHoleCardsA5swap(altBoardKey, holePair[0], holePair[1]);
          		  } else {
          			  alternateHandRank = handeval.tables.HandRankEvaluator.evaluateWithHoleCards(altBoardKey, holePair[0], holePair[1]);
          		  }
          	  } else {
          		  if ( (holePair[0] > 47) || (holePair[1] > 47)) {
          			  alternateHandRank = handeval.tables.HandRankEvaluator.evaluateWithHoleCardsA5swap(sampledHandIdx, holePair[0], holePair[1]);
          		  } else {
          			  alternateHandRank = handeval.tables.HandRankEvaluator.evaluateWithHoleCards(sampledHandIdx, holePair[0], holePair[1]);
          		  }
          	  }

                evaluatedRank = Math.max(evaluatedRank, alternateHandRank);

                if ( ((evaluatedRank >= 5863) && (evaluatedRank < 7140)) || (evaluatedRank >= 7296) ) {
              	  evaluatedRank += 2000;
                }
            }

            playerRanks[holeBase] = evaluatedRank;

            if (playerRanks[holeBase] > randomCard) {
               randomCard = playerRanks[holeBase];
               boardSlotPos = 1;
            } else if (playerRanks[holeBase] == randomCard) {
               ++boardSlotPos;
            }
         }

         for(holeBase = 0; holeBase < playerRanks.length; ++holeBase) {
            double equityShare = playerRanks[holeBase] == randomCard ? 1.0D / (double)boardSlotPos : 0.0D;
            double[] playerEquitySum = (double[])equitySumsList.get(holeBase);
            playerEquitySum[chosenHandIdx[holeBase]] += equityShare;
            int incrementSideEffect = (int) ((double[])countSumsList.get(holeBase))[chosenHandIdx[holeBase]]++;
         }

         ++chartPanel.e;
         return;
      }

   }

   private static void simulatePloHiLoRound(EquityChartPanel chartPanel, List equitySumsList, List countSumsList, int playerCount, long baseDeadMask, int[] boardSlots, int[] loQualifierScratch, int[] playerHiRanks, int[] playerLoRanks, List handCardBytesList, List cumulativeFreqsList, XorShiftRandomGenerator rng, int[] chosenHandIdx, int[][] playerHoleCards, int cardsToDeal) {
      label105:
      while(!chartPanel.d) {
         long deadMask = baseDeadMask;

         int playerIter;
         int sampledHandIdx;
         int holeBase;
         int cardDeckIdx;
         for(playerIter = 0; playerIter < playerCount; ++playerIter) {
            byte[] handCardBytes = (byte[])handCardBytesList.get(playerIter);
            double[] cumulativeFreqs = (double[])cumulativeFreqsList.get(playerIter);

            for(sampledHandIdx = rng.nextInt(cumulativeFreqs.length); cumulativeFreqs[sampledHandIdx] < 1.0D && rng.nextDouble() > cumulativeFreqs[sampledHandIdx]; sampledHandIdx = rng.nextInt(cumulativeFreqs.length)) {
            }

            holeBase = sampledHandIdx << 2;
            if (isBitSetLong(deadMask, handCardBytes[holeBase]) || isBitSetLong(deadMask, handCardBytes[holeBase + 1]) || isBitSetLong(deadMask, handCardBytes[holeBase + 2]) || isBitSetLong(deadMask, handCardBytes[holeBase + 3])) {
               continue label105;
            }

            chosenHandIdx[playerIter] = sampledHandIdx;
            int[] holeFour;
            (holeFour = playerHoleCards[playerIter])[0] = handCardBytes[holeBase];
            holeFour[1] = handCardBytes[holeBase + 1];
            holeFour[2] = handCardBytes[holeBase + 2];
            holeFour[3] = handCardBytes[holeBase + 3];
            cardDeckIdx = holeFour[0];
            long holeMaskAccum = deadMask | 1L << cardDeckIdx;
            cardDeckIdx = holeFour[1];
            holeMaskAccum |= 1L << cardDeckIdx;
            cardDeckIdx = holeFour[2];
            holeMaskAccum |= 1L << cardDeckIdx;
            cardDeckIdx = holeFour[3];
            deadMask = holeMaskAccum | 1L << cardDeckIdx;
         }

         int randomCard;
         int boardSlotPos;
         for(playerIter = cardsToDeal - 1; playerIter >= 0; --playerIter) {
            for(randomCard = rng.nextInt(52); isBitSetLong(deadMask, randomCard); randomCard = rng.nextInt(52)) {
            }

            boardSlotPos = 4 + (5 - cardsToDeal) + playerIter;
            boardSlots[boardSlotPos] = randomCard;
            cardDeckIdx = boardSlots[boardSlotPos];
            deadMask |= 1L << cardDeckIdx;
         }

         randomCard = 32767;
         boardSlotPos = 0;
         sampledHandIdx = Integer.MIN_VALUE;
         holeBase = 1;

         int evalPlayer;
         for(evalPlayer = 0; evalPlayer < playerCount; ++evalPlayer) {
            int[] holeFour = playerHoleCards[evalPlayer];
            boardSlots[0] = holeFour[0];
            boardSlots[1] = holeFour[1];
            boardSlots[2] = holeFour[2];
            boardSlots[3] = holeFour[3];
            playerHiRanks[evalPlayer] = boardSlots.length == 7 ? handeval.tables.HandRankEvaluator.evaluateSevenCard(boardSlots) : -handeval.PloHandEvaluator.evaluatePlo4Hi(boardSlots);
            if (playerHiRanks[evalPlayer] > sampledHandIdx) {
               sampledHandIdx = playerHiRanks[evalPlayer];
               holeBase = 1;
            } else if (playerHiRanks[evalPlayer] == sampledHandIdx) {
               ++holeBase;
            }

            playerLoRanks[evalPlayer] = handeval.PloHandEvaluator.evaluateLowHand(boardSlots, loQualifierScratch);
            if (playerLoRanks[evalPlayer] < randomCard) {
               randomCard = playerLoRanks[evalPlayer];
               boardSlotPos = 1;
            } else if (playerLoRanks[evalPlayer] == randomCard && randomCard != 32767) {
               ++boardSlotPos;
            }
         }

         ++chartPanel.e;

         for(evalPlayer = 0; evalPlayer < playerHiRanks.length; ++evalPlayer) {
            double hiShare = playerHiRanks[evalPlayer] == sampledHandIdx ? 1.0D / (double)holeBase : 0.0D;
            if (boardSlotPos > 0) {
               double lowHandShare = playerLoRanks[evalPlayer] == randomCard ? 1.0D / (double)boardSlotPos : 0.0D;
               double combinedShare = hiShare + (playerLoRanks[evalPlayer] == randomCard ? 1.0D / (double)boardSlotPos : 0.0D);
               lowHandShare += hiShare;
               hiShare = combinedShare / 2.0D;
            }

            double[] playerEquitySum = (double[])equitySumsList.get(evalPlayer);
            playerEquitySum[chosenHandIdx[evalPlayer]] += hiShare;
            int incrementSideEffect = (int) ((double[])countSumsList.get(evalPlayer))[chosenHandIdx[evalPlayer]]++;
         }

         return;
      }

   }

   private static void simulatePlo4Round(EquityChartPanel chartPanel, List equitySumsList, List countSumsList, int playerCount, long baseDeadMask, int[] boardSlots, int[] playerRanks, List handCardBytesList, List cumulativeFreqsList, XorShiftRandomGenerator rng, int[] chosenHandIdx, int[][] playerHoleCards, int cardsToDeal) {
      label81:
      while(!chartPanel.d) {
         long deadMask = baseDeadMask;

         int playerIter;
         int sampledHandIdx;
         int holeBase;
         int[] holeFour;
         int cardDeckIdx;
         for(playerIter = 0; playerIter < playerCount; ++playerIter) {
            byte[] handCardBytes = (byte[])handCardBytesList.get(playerIter);
            double[] cumulativeFreqs = (double[])cumulativeFreqsList.get(playerIter);

            for(sampledHandIdx = rng.nextInt(cumulativeFreqs.length); cumulativeFreqs[sampledHandIdx] < 1.0D && rng.nextDouble() > cumulativeFreqs[sampledHandIdx]; sampledHandIdx = rng.nextInt(cumulativeFreqs.length)) {
            }

            holeBase = sampledHandIdx << 2;
            if (isBitSetLong(deadMask, handCardBytes[holeBase]) || isBitSetLong(deadMask, handCardBytes[holeBase + 1]) || isBitSetLong(deadMask, handCardBytes[holeBase + 2]) || isBitSetLong(deadMask, handCardBytes[holeBase + 3])) {
               continue label81;
            }

            chosenHandIdx[playerIter] = sampledHandIdx;
            (holeFour = playerHoleCards[playerIter])[0] = handCardBytes[holeBase];
            holeFour[1] = handCardBytes[holeBase + 1];
            holeFour[2] = handCardBytes[holeBase + 2];
            holeFour[3] = handCardBytes[holeBase + 3];
            cardDeckIdx = holeFour[0];
            long holeMaskAccum = deadMask | 1L << cardDeckIdx;
            cardDeckIdx = holeFour[1];
            holeMaskAccum |= 1L << cardDeckIdx;
            cardDeckIdx = holeFour[2];
            holeMaskAccum |= 1L << cardDeckIdx;
            cardDeckIdx = holeFour[3];
            deadMask = holeMaskAccum | 1L << cardDeckIdx;
         }

         int randomCard;
         int boardSlotPos;
         for(playerIter = cardsToDeal - 1; playerIter >= 0; --playerIter) {
            for(randomCard = rng.nextInt(52); isBitSetLong(deadMask, randomCard); randomCard = rng.nextInt(52)) {
            }

            boardSlotPos = 4 + (5 - cardsToDeal) + playerIter;
            boardSlots[boardSlotPos] = randomCard;
            cardDeckIdx = boardSlots[boardSlotPos];
            deadMask |= 1L << cardDeckIdx;
         }

         randomCard = Integer.MIN_VALUE;
         boardSlotPos = 1;
         sampledHandIdx = handeval.PloHandEvaluator.detectBoardFlushSuit(boardSlots);

         for(holeBase = 0; holeBase < playerCount; ++holeBase) {
            holeFour = playerHoleCards[holeBase];
            boardSlots[0] = holeFour[0];
            boardSlots[1] = holeFour[1];
            boardSlots[2] = holeFour[2];
            boardSlots[3] = holeFour[3];
            playerRanks[holeBase] = -handeval.PloHandEvaluator.evaluatePlo4HiWithSuit(boardSlots, sampledHandIdx);
            if (playerRanks[holeBase] > randomCard) {
               randomCard = playerRanks[holeBase];
               boardSlotPos = 1;
            } else if (playerRanks[holeBase] == randomCard) {
               ++boardSlotPos;
            }
         }

         ++chartPanel.e;

         for(holeBase = 0; holeBase < playerRanks.length; ++holeBase) {
            if (playerRanks[holeBase] == randomCard) {
               double[] playerEquitySum = (double[])equitySumsList.get(holeBase);
               playerEquitySum[chosenHandIdx[holeBase]] += 1.0D / (double)boardSlotPos;
            }

            int incrementSideEffect = (int) ((double[])countSumsList.get(holeBase))[chosenHandIdx[holeBase]]++;
         }

         System.lineSeparator();
         return;
      }

   }

   // 5-card version of simulatePlo4Round for 5-card PLO equity calculations
   private static void simulatePlo5Round(EquityChartPanel chartPanel, List equitySumsList, List countSumsList, int playerCount, long baseDeadMask, int[] boardSlots, int[] playerRanks, List handCardBytesList, List cumulativeFreqsList, XorShiftRandomGenerator rng, int[] chosenHandIdx, int[][] playerHoleCards, int cardsToDeal) {
      label81:
      while(!chartPanel.d) {
         long deadMask = baseDeadMask;

         int playerIter;
         int sampledHandIdx;
         int holeBase;
         int[] holeFive;
         int cardDeckIdx;
         for(playerIter = 0; playerIter < playerCount; ++playerIter) {
            byte[] handCardBytes = (byte[])handCardBytesList.get(playerIter);
            double[] cumulativeFreqs = (double[])cumulativeFreqsList.get(playerIter);

            for(sampledHandIdx = rng.nextInt(cumulativeFreqs.length); cumulativeFreqs[sampledHandIdx] < 1.0D && rng.nextDouble() > cumulativeFreqs[sampledHandIdx]; sampledHandIdx = rng.nextInt(cumulativeFreqs.length)) {
            }

            holeBase = sampledHandIdx * 5;
            if (isBitSetLong(deadMask, handCardBytes[holeBase]) || isBitSetLong(deadMask, handCardBytes[holeBase + 1]) || isBitSetLong(deadMask, handCardBytes[holeBase + 2]) || isBitSetLong(deadMask, handCardBytes[holeBase + 3]) || isBitSetLong(deadMask, handCardBytes[holeBase + 4])) {
               continue label81;
            }

            chosenHandIdx[playerIter] = sampledHandIdx;
            holeFive = playerHoleCards[playerIter];
            holeFive[0] = handCardBytes[holeBase];
            holeFive[1] = handCardBytes[holeBase + 1];
            holeFive[2] = handCardBytes[holeBase + 2];
            holeFive[3] = handCardBytes[holeBase + 3];
            holeFive[4] = handCardBytes[holeBase + 4];

            cardDeckIdx = holeFive[0];
            long holeMaskAccum = deadMask | 1L << cardDeckIdx;
            cardDeckIdx = holeFive[1];
            holeMaskAccum |= 1L << cardDeckIdx;
            cardDeckIdx = holeFive[2];
            holeMaskAccum |= 1L << cardDeckIdx;
            cardDeckIdx = holeFive[3];
            holeMaskAccum |= 1L << cardDeckIdx;
            cardDeckIdx = holeFive[4];
            deadMask = holeMaskAccum | 1L << cardDeckIdx;
         }

         int randomCard;
         int boardSlotPos;
         for(playerIter = cardsToDeal - 1; playerIter >= 0; --playerIter) {
            for(randomCard = rng.nextInt(52); isBitSetLong(deadMask, randomCard); randomCard = rng.nextInt(52)) {
            }

            boardSlotPos = 5 + (5 - cardsToDeal) + playerIter;
            boardSlots[boardSlotPos] = randomCard;
            cardDeckIdx = boardSlots[boardSlotPos];
            deadMask |= 1L << cardDeckIdx;
         }

         randomCard = Integer.MIN_VALUE;
         boardSlotPos = 1;
         sampledHandIdx = handeval.PloHandEvaluator.detectBoardFlushSuit5c(boardSlots);

         for(holeBase = 0; holeBase < playerCount; ++holeBase) {
            holeFive = playerHoleCards[holeBase];
            boardSlots[0] = holeFive[0];
            boardSlots[1] = holeFive[1];
            boardSlots[2] = holeFive[2];
            boardSlots[3] = holeFive[3];
            boardSlots[4] = holeFive[4];
            playerRanks[holeBase] = -handeval.PloHandEvaluator.evaluatePlo5HiWithSuit(boardSlots, sampledHandIdx);
            if (playerRanks[holeBase] > randomCard) {
               randomCard = playerRanks[holeBase];
               boardSlotPos = 1;
            } else if (playerRanks[holeBase] == randomCard) {
               ++boardSlotPos;
            }
         }

         ++chartPanel.e;

         for(holeBase = 0; holeBase < playerRanks.length; ++holeBase) {
            if (playerRanks[holeBase] == randomCard) {
               double[] playerEquitySum = (double[])equitySumsList.get(holeBase);
               playerEquitySum[chosenHandIdx[holeBase]] += 1.0D / (double)boardSlotPos;
            }

            int incrementSideEffect = (int) ((double[])countSumsList.get(holeBase))[chosenHandIdx[holeBase]]++;
         }

         System.lineSeparator();
         return;
      }

   }

   public static boolean cardInArray(card target, card[] cards) {
      card[] cardArray = cards;
      int length = cards.length;

      for(int idx = 0; idx < length; ++idx) {
         card candidate = cardArray[idx];
         if (target.cardEquals(candidate)) {
            return true;
         }
      }

      return false;
   }

   public static void generate2EquityShortdeck(boolean isShortDeck) throws Throwable {
      EquityTableCache.loadEquityTables();
      TLongDoubleHashMap riverEquityMap = new TLongDoubleHashMap(14661258, 1.0F);
      TLongDoubleHashMap turnEquityMap = new TLongDoubleHashMap(3635295, 1.0F);
      TLongDoubleHashMap flopEquityMap = new TLongDoubleHashMap(418133, 1.0F);
      TLongDoubleHashMap preflopEquityMap = new TLongDoubleHashMap(170, 1.0F);
      card[] boardScratch = new card[7];

      for(int boardInit = 0; boardInit < 7; ++boardInit) {
         boardScratch[boardInit] = new card(0, 0);
      }

      int[] suitScratch = new int[4];
      card[] handScratch = new card[7];
      Iterator startingHandsIter = CardArrays.getStartingHandsListHoldemShortdeck().iterator();

      int count = 0;
      while(startingHandsIter.hasNext()) {
         card[] startingHand = (card[])startingHandsIter.next();
         System.lineSeparator();
         Long preflopKey = (new CardCombinations(startingHand, (byte)0)).computeHash();
         double preflopVarianceSum = 0.0D;
         double flopCount = 0.0D;
         handScratch[0] = startingHand[0];
         handScratch[1] = startingHand[1];

         double flopEquity;
         for(Iterator flopIter = CardArrays.generateWholeFlopsListShortdeck(startingHand).iterator(); flopIter.hasNext(); preflopVarianceSum += flopEquity * flopEquity) {
            card[] flop = (card[])flopIter.next();
            ++flopCount;
            Arrays.fill(suitScratch, 0);
            long flopKey = CardCombinations.computeHandHashByLength(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2]}, boardScratch, suitScratch, 5);
            flopEquity = 0.0D;
            double flopVarianceSum = 0.0D;
            double turnCount = 0.0D;
            handScratch[2] = flop[0];
            handScratch[3] = flop[1];
            handScratch[4] = flop[2];

            double turnEquity;
            for(Iterator turnIter = CardArrays.getRestCardsShortdeck(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2]}).iterator(); turnIter.hasNext(); flopVarianceSum += turnEquity * turnEquity) {
               card turnCard = (card)turnIter.next();
               ++turnCount;
               Arrays.fill(suitScratch, 0);
               long turnKey = CardCombinations.computeHandHashByLength(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2], turnCard}, boardScratch, suitScratch, 6);
               turnEquity = 0.0D;
               double turnVarianceSum = 0.0D;
               double riverCount = 0.0D;
               handScratch[5] = turnCard;

               double riverEquity;
               for(Iterator riverIter = CardArrays.getRestCardsShortdeck(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2], turnCard}).iterator(); riverIter.hasNext(); turnVarianceSum += riverEquity * riverEquity) {
                  turnCard = (card)riverIter.next();
                  ++riverCount;
                  handScratch[6] = turnCard;
                  long riverKey = CardCombinations.computeRiverHandHash(handScratch, suitScratch);
                  riverEquity = EquityTableCache.e.get(riverKey);
                  turnEquity += riverEquity;
               }

               turnEquity /= riverCount;
               turnVarianceSum /= riverCount;
               turnEquityMap.put(turnKey, turnVarianceSum);
               flopEquity += turnEquity;
            }

            flopEquity /= turnCount;
            flopVarianceSum /= turnCount;
            flopEquityMap.put(flopKey, flopVarianceSum);
         }

         preflopVarianceSum /= flopCount;
         preflopEquityMap.put(preflopKey, preflopVarianceSum);

         count++;
         //System.out.println(count);
         MainTabbedPane.setStatusWithProgress("Generating equity2SD",count,630);
      }

      util.AppFile baseDir = isShortDeck ? new util.AppFile("HoldemSixPlus") : new util.AppFile();

      try {
         writeObject((File)(new util.AppFile(baseDir, "riverequity2SD")), (Object)riverEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "turnequity2SD")), (Object)turnEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "flopequity2SD")), (Object)flopEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "preflopequity2SD")), (Object)preflopEquityMap);
      } catch (Exception ignored) {
      }
   }

   public static void generate2Equity(boolean isShortDeck) throws Throwable {
      EquityTableCache.loadEquityTables();
      TLongDoubleHashMap riverEquityMap = new TLongDoubleHashMap(14661258, 1.0F);
      TLongDoubleHashMap turnEquityMap = new TLongDoubleHashMap(3635295, 1.0F);
      TLongDoubleHashMap flopEquityMap = new TLongDoubleHashMap(418133, 1.0F);
      TLongDoubleHashMap preflopEquityMap = new TLongDoubleHashMap(170, 1.0F);
      card[] boardScratch = new card[7];

      for(int boardInit = 0; boardInit < 7; ++boardInit) {
         boardScratch[boardInit] = new card(0, 0);
      }

      int[] suitScratch = new int[4];
      card[] handScratch = new card[7];
      Iterator startingHandsIter = CardArrays.getStartingHandsListHoldem().iterator();

      int count = 0;
      while(startingHandsIter.hasNext()) {
         card[] startingHand = (card[])startingHandsIter.next();
         System.lineSeparator();
         Long preflopKey = (new CardCombinations(startingHand, (byte)0)).computeHash();
         double preflopVarianceSum = 0.0D;
         double flopCount = 0.0D;
         handScratch[0] = startingHand[0];
         handScratch[1] = startingHand[1];

         double flopEquity;
         for(Iterator flopIter = CardArrays.generateWholeFlopsList(startingHand).iterator(); flopIter.hasNext(); preflopVarianceSum += flopEquity * flopEquity) {
            card[] flop = (card[])flopIter.next();
            ++flopCount;
            Arrays.fill(suitScratch, 0);
            long flopKey = CardCombinations.computeHandHashByLength(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2]}, boardScratch, suitScratch, 5);
            flopEquity = 0.0D;
            double flopVarianceSum = 0.0D;
            double turnCount = 0.0D;
            handScratch[2] = flop[0];
            handScratch[3] = flop[1];
            handScratch[4] = flop[2];

            double turnEquity;
            for(Iterator turnIter = CardArrays.getRestCardsFullDeck(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2]}).iterator(); turnIter.hasNext(); flopVarianceSum += turnEquity * turnEquity) {
               card turnCard = (card)turnIter.next();
               ++turnCount;
               Arrays.fill(suitScratch, 0);
               long turnKey = CardCombinations.computeHandHashByLength(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2], turnCard}, boardScratch, suitScratch, 6);
               turnEquity = 0.0D;
               double turnVarianceSum = 0.0D;
               double riverCount = 0.0D;
               handScratch[5] = turnCard;

               double riverEquity;
               for(Iterator riverIter = CardArrays.getRestCardsFullDeck(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2], turnCard}).iterator(); riverIter.hasNext(); turnVarianceSum += riverEquity * riverEquity) {
                  turnCard = (card)riverIter.next();
                  ++riverCount;
                  handScratch[6] = turnCard;
                  long riverKey = CardCombinations.computeRiverHandHash(handScratch, suitScratch);
                  riverEquity = EquityTableCache.e.get(riverKey);
                  turnEquity += riverEquity;
               }

               turnEquity /= riverCount;
               turnVarianceSum /= riverCount;
               turnEquityMap.put(turnKey, turnVarianceSum);
               flopEquity += turnEquity;
            }

            flopEquity /= turnCount;
            flopVarianceSum /= turnCount;
            flopEquityMap.put(flopKey, flopVarianceSum);
         }

         preflopVarianceSum /= flopCount;
         preflopEquityMap.put(preflopKey, preflopVarianceSum);

         count++;
         MainTabbedPane.setStatusWithProgress("Generating equity2",count,1326);
      }

      util.AppFile baseDir = isShortDeck ? new util.AppFile("HoldemSixPlus") : new util.AppFile();

      try {
         writeObject((File)(new util.AppFile(baseDir, "riverequity2")), (Object)riverEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "turnequity2")), (Object)turnEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "flopequity2")), (Object)flopEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "preflopequity2")), (Object)preflopEquityMap);
      } catch (Exception ignored) {
      }
   }

   public static void generate2EquityTest(boolean isShortDeck) throws Throwable {
      //e.a();
      TLongDoubleHashMap riverEquityMap = new TLongDoubleHashMap(14661258, 1.0F);
      TLongDoubleHashMap turnEquityMap = new TLongDoubleHashMap(3635295, 1.0F);
      TLongDoubleHashMap flopEquityMap = new TLongDoubleHashMap(418133, 1.0F);
      TLongDoubleHashMap preflopEquityMap = new TLongDoubleHashMap(170, 1.0F);
      card[] boardScratch = new card[7];

      for(int boardInit = 0; boardInit < 7; ++boardInit) {
         boardScratch[boardInit] = new card(0, 0);
      }

      int[] suitScratch = new int[4];
      card[] handScratch = new card[7];
      Iterator startingHandsIter = CardArrays.getStartingHandsListHoldem().iterator();

      int curNum = 0;
      while(startingHandsIter.hasNext()) {
         card[] startingHand = (card[])startingHandsIter.next();
         System.lineSeparator();
         Long preflopKey = (new CardCombinations(startingHand, (byte)0)).computeHash();
         double preflopVarianceSum = 0.0D;
         double flopCount = 0.0D;
         handScratch[0] = startingHand[0];
         handScratch[1] = startingHand[1];

         double flopEquity;
         for(Iterator flopIter = CardArrays.generateWholeFlopsList(startingHand).iterator(); flopIter.hasNext(); preflopVarianceSum += flopEquity * flopEquity) {
            card[] flop = (card[])flopIter.next();
            ++flopCount;
            Arrays.fill(suitScratch, 0);
            long flopKey = CardCombinations.computeHandHashByLength(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2]}, boardScratch, suitScratch, 5);
            flopEquity = 0.0D;
            double flopVarianceSum = 0.0D;
            double turnCount = 0.0D;
            handScratch[2] = flop[0];
            handScratch[3] = flop[1];
            handScratch[4] = flop[2];

            double turnEquity;
            for(Iterator turnIter = CardArrays.getRestCardsFullDeck(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2]}).iterator(); turnIter.hasNext(); flopVarianceSum += turnEquity * turnEquity) {
               card turnCard = (card)turnIter.next();
               ++turnCount;
               Arrays.fill(suitScratch, 0);
               long turnKey = CardCombinations.computeHandHashByLength(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2], turnCard}, boardScratch, suitScratch, 6);
               turnEquity = 0.0D;
               double turnVarianceSum = 0.0D;
               double riverCount = 0.0D;
               handScratch[5] = turnCard;

               double riverEquity;
               for(Iterator riverIter = CardArrays.getRestCardsFullDeck(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2], turnCard}).iterator(); riverIter.hasNext(); turnVarianceSum += riverEquity * riverEquity) {
                  turnCard = (card)riverIter.next();
                  ++riverCount;
                  handScratch[6] = turnCard;
                  long riverKey = CardCombinations.computeRiverHandHash(handScratch, suitScratch);
                  riverEquity = EquityTableCache.e.get(riverKey);
                  turnEquity += riverEquity;
               }

               turnEquity /= riverCount;
               turnVarianceSum /= riverCount;
               turnEquityMap.put(turnKey, turnVarianceSum);
               flopEquity += turnEquity;
            }

            flopEquity /= turnCount;
            flopVarianceSum /= turnCount;
            flopEquityMap.put(flopKey, flopVarianceSum);
         }

         preflopVarianceSum /= flopCount;
         preflopEquityMap.put(preflopKey, preflopVarianceSum);


         MainTabbedPane.k.setTitle( Integer.toString(curNum) + " from 1326");
         curNum++;
      }

      util.AppFile baseDir = isShortDeck ? new util.AppFile("HoldemSixPlus") : new util.AppFile();

      try {
         writeObject((File)(new util.AppFile(baseDir, "riverequity2")), (Object)riverEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "turnequity2")), (Object)turnEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "flopequity2")), (Object)flopEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "preflopequity2")), (Object)preflopEquityMap);
      } catch (Exception ignored) {
      }
   }

   public static void generate1Equity(boolean isShortDeck) throws Throwable {
      TLongDoubleHashMap riverEquityMap = new TLongDoubleHashMap();
      TLongDoubleHashMap turnEquityMap = new TLongDoubleHashMap();
      TLongDoubleHashMap flopEquityMap = new TLongDoubleHashMap();
      TLongDoubleHashMap preflopEquityMap = new TLongDoubleHashMap();
      handeval.tables.HandRankEvaluator.initialize();
      card[] boardScratch = new card[7];

      for(int boardInit = 0; boardInit < 7; ++boardInit) {
         boardScratch[boardInit] = new card(0, 0);
      }

      int[] suitScratch = new int[4];
      int[] evalScratch5 = new int[5];
      int[] evalScratch7a = new int[7];
      int[] evalScratch7b = new int[7];
      int[] indexScratch = new int[7];
      card[] handScratch = new card[7];
      Iterator startingHandsIter = CardArrays.getStartingHandsListHoldem().iterator();

      card[] startingHand;
      int count = 0;
      while(startingHandsIter.hasNext()) {
         startingHand = (card[])startingHandsIter.next();
         System.lineSeparator();
         long preflopKey = (new CardCombinations(startingHand, (byte)0)).computeHash();
         double preflopEquitySum = 0.0D;
         double flopCount = 0.0D;
         handScratch[0] = startingHand[0];
         handScratch[1] = startingHand[1];

         double flopEquity;
         for(Iterator flopIter = CardArrays.generateWholeFlopsList(startingHand).iterator(); flopIter.hasNext(); preflopEquitySum += flopEquity) {
            card[] flop = (card[])flopIter.next();
            ++flopCount;
            Arrays.fill(suitScratch, 0);
            long flopKey = CardCombinations.computeHandHashByLength(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2]}, boardScratch, suitScratch, 5);
            handScratch[2] = flop[0];
            handScratch[3] = flop[1];
            handScratch[4] = flop[2];
            flopEquity = 0.0D;
            double turnCount = 0.0D;

            double turnEquity;
            for(Iterator turnIter = CardArrays.getRestCardsFullDeck(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2]}).iterator(); turnIter.hasNext(); flopEquity += turnEquity) {
               card turnCard = (card)turnIter.next();
               ++turnCount;
               Arrays.fill(suitScratch, 0);
               long turnKey = CardCombinations.computeHandHashByLength(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2], turnCard}, boardScratch, suitScratch, 6);
               handScratch[5] = turnCard;
               turnEquity = 0.0D;
               double riverCount = 0.0D;

               double riverEquity;
               for(Iterator riverIter = CardArrays.getRestCardsFullDeck(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2], turnCard}).iterator(); riverIter.hasNext(); turnEquity += riverEquity) {
                  card riverCard = (card)riverIter.next();
                  ++riverCount;
                  handScratch[6] = riverCard;
                  long riverKey = CardCombinations.computeRiverHandHash(handScratch, suitScratch);
                  if (riverEquityMap.containsKey(riverKey)) {
                     riverEquity = riverEquityMap.get(riverKey);
                  } else {
                     indexScratch[2] = flop[0].getFullDeckIndex();
                     indexScratch[3] = flop[1].getFullDeckIndex();
                     indexScratch[4] = flop[2].getFullDeckIndex();
                     indexScratch[5] = turnCard.getFullDeckIndex();
                     indexScratch[6] = riverCard.getFullDeckIndex();
                     evalScratch7b[2] = flop[0].getShortdeckIndex();
                     evalScratch7b[3] = flop[1].getShortdeckIndex();
                     evalScratch7b[4] = flop[2].getShortdeckIndex();
                     evalScratch7b[5] = turnCard.getShortdeckIndex();
                     evalScratch7b[6] = riverCard.getShortdeckIndex();
                     double winsCount = 0.0D;
                     double lossesCount = 0.0D;
                     double tiesCount = 0.0D;
                     Iterator opponentIter = CardArrays.getAllHoldemHandsExcluding(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2], turnCard, riverCard}).iterator();

                     while(opponentIter.hasNext()) {
                        card[] opponentHand = (card[])opponentIter.next();
                        int heroRank;
                        int villainRank;
                        if (isShortDeck) {
                           evalScratch7b[0] = opponentHand[0].getShortdeckIndex();
                           evalScratch7b[1] = opponentHand[1].getShortdeckIndex();
                           villainRank = handeval.HandRankHelper.evaluateSevenCardWithCopy(evalScratch7b, evalScratch5, evalScratch7a);
                           evalScratch7b[0] = startingHand[0].getShortdeckIndex();
                           evalScratch7b[1] = startingHand[1].getShortdeckIndex();
                           heroRank = handeval.HandRankHelper.evaluateSevenCardWithCopy(evalScratch7b, evalScratch5, evalScratch7a);
                        } else {
                           indexScratch[0] = opponentHand[0].getFullDeckIndex();
                           indexScratch[1] = opponentHand[1].getFullDeckIndex();
                           villainRank = handeval.tables.HandRankEvaluator.evaluateSevenCard(indexScratch);
                           indexScratch[0] = startingHand[0].getFullDeckIndex();
                           indexScratch[1] = startingHand[1].getFullDeckIndex();
                           heroRank = handeval.tables.HandRankEvaluator.evaluateSevenCard(indexScratch);
                        }

                        if (heroRank > villainRank) {
                           ++winsCount;
                        } else if (villainRank > heroRank) {
                           ++lossesCount;
                        } else {
                           ++tiesCount;
                        }
                     }

                     riverEquity = (winsCount + tiesCount / 2.0D) / (winsCount + lossesCount + tiesCount);
                     riverEquityMap.put(riverKey, riverEquity);
                  }
               }

               turnEquity /= riverCount;
               if (!turnEquityMap.containsKey(turnKey)) {
                  turnEquityMap.put(turnKey, turnEquity);
               }
            }

            flopEquity /= turnCount;
            if (!flopEquityMap.containsKey(flopKey)) {
               flopEquityMap.put(flopKey, flopEquity);
            }
         }

         preflopEquitySum /= flopCount;
         if (!preflopEquityMap.containsKey(preflopKey)) {
            preflopEquityMap.put(preflopKey, preflopEquitySum);
         }

         count++;
         MainTabbedPane.setStatusWithProgress("Generating equity",count,1326);
      }

      startingHandsIter = CardArrays.getStartingHandsListHoldem().iterator();

      while(startingHandsIter.hasNext()) {
         startingHand = (card[])startingHandsIter.next();
         new CardCombinations(startingHand, (byte)0);
         System.lineSeparator();
      }

      util.AppFile baseDir = isShortDeck ? new util.AppFile("HoldemSixPlus") : new util.AppFile();

      try {
         writeObject((File)(new util.AppFile(baseDir, "riverequity")), (Object)riverEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "turnequity")), (Object)turnEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "flopequity")), (Object)flopEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "preflopequity")), (Object)preflopEquityMap);
      } catch (Exception ignored) {
      }
   }
   
   public static void generate1EquityShortdeck(boolean isShortDeck) throws Throwable {
      TLongDoubleHashMap riverEquityMap = new TLongDoubleHashMap();
      TLongDoubleHashMap turnEquityMap = new TLongDoubleHashMap();
      TLongDoubleHashMap flopEquityMap = new TLongDoubleHashMap();
      TLongDoubleHashMap preflopEquityMap = new TLongDoubleHashMap();
      handeval.tables.HandRankEvaluator.initialize();
      card[] boardScratch = new card[7];

      for(int boardInit = 0; boardInit < 7; ++boardInit) {
         boardScratch[boardInit] = new card(0, 0);
      }

      int[] suitScratch = new int[4];
      int[] evalScratch5 = new int[5];
      int[] evalScratch7a = new int[7];
      int[] evalScratch7b = new int[7];
      int[] indexScratch = new int[7];
      int[] indexScratchTmp = new int[7];
      card[] handScratch = new card[7];
      Iterator startingHandsIter = CardArrays.getStartingHandsListHoldemShortdeck().iterator();

      card[] startingHand;
      int count = 0;
      while(startingHandsIter.hasNext()) {
         startingHand = (card[])startingHandsIter.next();
         System.lineSeparator();
         long preflopKey = (new CardCombinations(startingHand, (byte)0)).computeHash();
         double preflopEquitySum = 0.0D;
         double flopCount = 0.0D;
         handScratch[0] = startingHand[0];
         handScratch[1] = startingHand[1];

         double flopEquity;
         for(Iterator flopIter = CardArrays.generateWholeFlopsListShortdeck(startingHand).iterator(); flopIter.hasNext(); preflopEquitySum += flopEquity) {
            card[] flop = (card[])flopIter.next();
            ++flopCount;
            Arrays.fill(suitScratch, 0);
            long flopKey = CardCombinations.computeHandHashByLength(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2]}, boardScratch, suitScratch, 5);
            handScratch[2] = flop[0];
            handScratch[3] = flop[1];
            handScratch[4] = flop[2];
            flopEquity = 0.0D;
            double turnCount = 0.0D;

            double turnEquity;
            for(Iterator turnIter = CardArrays.getRestCardsShortdeck(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2]}).iterator(); turnIter.hasNext(); flopEquity += turnEquity) {
               card turnCard = (card)turnIter.next();
               ++turnCount;
               Arrays.fill(suitScratch, 0);
               long turnKey = CardCombinations.computeHandHashByLength(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2], turnCard}, boardScratch, suitScratch, 6);
               handScratch[5] = turnCard;
               turnEquity = 0.0D;
               double riverCount = 0.0D;

               double riverEquity;
               for(Iterator riverIter = CardArrays.getRestCardsShortdeck(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2], turnCard}).iterator(); riverIter.hasNext(); turnEquity += riverEquity) {
                  card riverCard = (card)riverIter.next();
                  ++riverCount;
                  handScratch[6] = riverCard;
                  long riverKey = CardCombinations.computeRiverHandHash(handScratch, suitScratch);
                  if (riverEquityMap.containsKey(riverKey)) {
                     riverEquity = riverEquityMap.get(riverKey);
                  } else {
                     indexScratch[2] = flop[0].getFullDeckIndex();
                     indexScratch[3] = flop[1].getFullDeckIndex();
                     indexScratch[4] = flop[2].getFullDeckIndex();
                     indexScratch[5] = turnCard.getFullDeckIndex();
                     indexScratch[6] = riverCard.getFullDeckIndex();
                     evalScratch7b[2] = flop[0].getShortdeckIndex();
                     evalScratch7b[3] = flop[1].getShortdeckIndex();
                     evalScratch7b[4] = flop[2].getShortdeckIndex();
                     evalScratch7b[5] = turnCard.getShortdeckIndex();
                     evalScratch7b[6] = riverCard.getShortdeckIndex();
                     double winsCount = 0.0D;
                     double lossesCount = 0.0D;
                     double tiesCount = 0.0D;
                     Iterator opponentIter = CardArrays.eShortdeck(new card[]{startingHand[0], startingHand[1], flop[0], flop[1], flop[2], turnCard, riverCard}).iterator();


                     while(opponentIter.hasNext()) {
                        card[] opponentHand = (card[])opponentIter.next();
                        int heroRank;
                        int villainRank;
                        int swap;

                        indexScratch[0] = opponentHand[0].getFullDeckIndex();
                        indexScratch[1] = opponentHand[1].getFullDeckIndex();
                        villainRank = handeval.tables.HandRankEvaluator.evaluateSevenCard(indexScratch);
                        if ( ((villainRank >= 5863) && (villainRank < 7140)) || (villainRank >= 7296) ) {
                        	villainRank += 2000;
		                }

                        indexScratch[0] = startingHand[0].getFullDeckIndex();
                        indexScratch[1] = startingHand[1].getFullDeckIndex();
                        heroRank = handeval.tables.HandRankEvaluator.evaluateSevenCard(indexScratch);
                        if ( ((heroRank >= 5863) && (heroRank < 7140)) || (heroRank >= 7296) ) {
                        	heroRank += 2000;
		                }

                        /*if ( (( villainRank >= 5863 ) && ( villainRank < 7140)) && (( heroRank >= 7140 ) && ( heroRank < 7296)) ) {
                        	swap = villainRank;
                        	villainRank = heroRank;
                        	heroRank = swap;
                        } else {
                        	if ( (( heroRank >= 5863 ) && ( heroRank < 7140)) && (( villainRank >= 7140 ) && ( villainRank < 7296)) ) {
                            	swap = villainRank;
                            	villainRank = heroRank;
                            	heroRank = swap;
                            }
                        }*/

                        if (heroRank > villainRank) {
                           ++winsCount;
                        } else if (villainRank > heroRank) {
                           ++lossesCount;
                        } else {
                           ++tiesCount;
                        }
                     }

                     riverEquity = (winsCount + tiesCount / 2.0D) / (winsCount + lossesCount + tiesCount);
                     riverEquityMap.put(riverKey, riverEquity);
                  }
               }

               turnEquity /= riverCount;
               if (!turnEquityMap.containsKey(turnKey)) {
                  turnEquityMap.put(turnKey, turnEquity);
               }
            }

            flopEquity /= turnCount;
            if (!flopEquityMap.containsKey(flopKey)) {
               flopEquityMap.put(flopKey, flopEquity);
            }
         }

         preflopEquitySum /= flopCount;
         if (!preflopEquityMap.containsKey(preflopKey)) {
            preflopEquityMap.put(preflopKey, preflopEquitySum);
         }

         count++;
         //System.out.println(count);
         MainTabbedPane.setStatusWithProgress("Generating equitySD",count,630);
      }

      startingHandsIter = CardArrays.getStartingHandsListHoldemShortdeck().iterator();

      while(startingHandsIter.hasNext()) {
         startingHand = (card[])startingHandsIter.next();
         new CardCombinations(startingHand, (byte)0);
         System.lineSeparator();
      }

      util.AppFile baseDir = /*isShortDeck ? new util.AppFile("HoldemSixPlus") :*/ new util.AppFile();

      try {
         writeObject((File)(new util.AppFile(baseDir, "riverequitySD")), (Object)riverEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "turnequitySD")), (Object)turnEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "flopequitySD")), (Object)flopEquityMap);
         writeObject((File)(new util.AppFile(baseDir, "preflopequitySD")), (Object)preflopEquityMap);
      } catch (Exception ignored) {
      }
   }

   public static void generateRiverBuckets(int bucketCount) throws Throwable {
      EquityTableCache.loadEquityTables();
      TLongIntHashMap riverBucketMap = new TLongIntHashMap(EquityTableCache.e.size());
      int[] suitScratch = new int[4];
      card[] boardScratch = new card[7];

      int processedCount;
      for(processedCount = 0; processedCount < 7; ++processedCount) {
         boardScratch[processedCount] = new card(0, 0);
      }

      processedCount = 0;
      boardScratch = new card[7];

      for(int boardInit = 0; boardInit < 7; ++boardInit) {
         boardScratch[boardInit] = new card(0, 0);
      }

      AnalysisPanel.isDebugMode();
      List allBoards;
      if (AnalysisPanel.gameType == 3) {
    	  allBoards = CardArrays.bShortdeck();
      } else {
    	  allBoards = CardArrays.getAllFlushBoards();
      }

      Iterator boardIter = allBoards.iterator();

      label62:
      while(boardIter.hasNext()) {
         card[] board = (card[])boardIter.next();
         if (processedCount % 100 == 0) {
            System.lineSeparator();
            MainTabbedPane.setStatusWithProgress("Generating river buckets.", processedCount, allBoards.size());
         }

         ++processedCount;

         for(int boardCopy = 0; boardCopy < 5; ++boardCopy) {
            boardScratch[boardCopy + 2] = board[boardCopy];
         }

         Iterator handIter;

         if (AnalysisPanel.gameType == 3) {
        	 handIter = CardArrays.eShortdeck(board).iterator();
         } else {
        	 handIter = CardArrays.getAllHoldemHandsExcluding(board).iterator();
         }

         while(true) {
            long comboKey;
            do {
               if (!handIter.hasNext()) {
                  continue label62;
               }

               board = (card[])handIter.next();
               boardScratch[0] = board[0];
               boardScratch[1] = board[1];
               comboKey = CardCombinations.computeRiverHandHash(boardScratch, suitScratch);
            } while(riverBucketMap.containsKey(comboKey));

            double bucketCountD = (double)bucketCount;
            double equityValue = EquityTableCache.e.get(comboKey);
            double bucketMax = bucketCountD;

            int bucketIter;
            int bucketIdx;
            label57: {
               for(bucketIter = 1; (double)bucketIter <= bucketMax; ++bucketIter) {
                  if (equityValue <= 0.0D + 1.0D * ((double)bucketIter / bucketMax)) {
                     bucketIdx = bucketIter - 1;
                     break label57;
                  }
               }

               System.lineSeparator();
               bucketIdx = -1;
            }

            bucketIter = bucketIdx;
            riverBucketMap.put(comboKey, bucketIter);
         }
      }

      try {
         util.AppFile baseDir = new util.AppFile();
         ObjectOutputStream out;
         String sd = ""; if (AnalysisPanel.gameType == 3) { sd = "SD"; }
         (out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new util.AppFile(baseDir, "mmnestedriver"+sd+"." + bucketCount))))).writeObject(riverBucketMap);
         out.close();
      } catch (Exception ignored) {
      }
   }

   public static void generateTurnBuckets(int equityBuckets, int unused) throws Throwable {
      System.lineSeparator();
      EquityTableCache.loadEquityTables();
      EquityTableCache.loadEquity2Tables();
      List allTurns;

      if (AnalysisPanel.gameType == 3) {
    	  allTurns = CardArrays.getAllTurnsShortdeck();
      } else {
    	  allTurns = CardArrays.getAllTurns();
      }
      TLongIntHashMap turnBucketMap = new TLongIntHashMap(EquityTableCache.c.size());
      TIntDoubleHashMap bucketMaxMap = new TIntDoubleHashMap();
      TIntDoubleHashMap bucketMinMap = new TIntDoubleHashMap();
      gnu.wrapper.set.LongSet visitedSet = new gnu.wrapper.set.LongSet();
      gnu.wrapper.list.LongList comboKeys = new gnu.wrapper.list.LongList();
      int turnIdx = 0;
      card[] boardSlots = new card[7];
      AnalysisPanel.isDebugMode();
      String pathPrefix = "";
      card[] boardScratch = new card[7];
      int[] suitScratch = new int[4];

      int scratchInit;
      for(scratchInit = 0; scratchInit < 7; ++scratchInit) {
         boardScratch[scratchInit] = new card(0, 0);
      }

      Iterator turnIter = allTurns.iterator();

      while(turnIter.hasNext()) {
         card[] turnBoard = (card[])turnIter.next();
         if (turnIdx % 100 == 0) {
            MainTabbedPane.setStatusWithProgress("Generating turn buckets.", turnIdx, allTurns.size());
         }

         ++turnIdx;
         List handList;
         if (AnalysisPanel.gameType == 3) {
        	 handList = CardArrays.eShortdeck(turnBoard);
         } else {
        	 handList = CardArrays.getAllHoldemHandsExcluding(turnBoard);
         }

         double maxEquity = 0.0D;
         double minEquity = 1.0D;
         comboKeys.clear();
         boardSlots[2] = turnBoard[0];
         boardSlots[3] = turnBoard[1];
         boardSlots[4] = turnBoard[2];
         boardSlots[5] = turnBoard[3];

         double equityValue;
         for(Iterator handIter = handList.iterator(); handIter.hasNext(); minEquity = Math.min(minEquity, equityValue)) {
            turnBoard = (card[])handIter.next();
            boardSlots[0] = turnBoard[0];
            boardSlots[1] = turnBoard[1];
            long comboKey = CardCombinations.computeTurnHandHash(boardSlots, suitScratch);
            comboKeys.add(comboKey);
            equityValue = EquityTableCache.c.get(comboKey);
            maxEquity = Math.max(maxEquity, equityValue);
         }

         bucketMaxMap.clear();
         bucketMinMap.clear();

         long comboKey;
         for(scratchInit = 0; scratchInit < comboKeys.size(); ++scratchInit) {
            comboKey = comboKeys.get(scratchInit);
            if (!turnBucketMap.containsKey(comboKey)) {
               double comboEquity = EquityTableCache.c.get(comboKey);
               double comboVariance = EquityTableCache.d.get(comboKey);
               int bucketIdx = computeBucketIndex(comboEquity, (double)equityBuckets, minEquity, maxEquity);
               turnBucketMap.put(comboKey, bucketIdx);
               if (!bucketMaxMap.containsKey(bucketIdx) || comboVariance > bucketMaxMap.get(bucketIdx)) {
                  bucketMaxMap.put(bucketIdx, comboVariance);
               }

               if (!bucketMinMap.containsKey(bucketIdx) || comboVariance < bucketMinMap.get(bucketIdx)) {
                  bucketMinMap.put(bucketIdx, comboVariance);
               }
            }
         }

         for(scratchInit = 0; scratchInit < comboKeys.size(); ++scratchInit) {
            comboKey = comboKeys.get(scratchInit);
            if (!visitedSet.contains(comboKey)) {
               visitedSet.add(comboKey);
               int currentBucket = turnBucketMap.get(comboKey);
               int subBucket = computeBucketIndex(EquityTableCache.d.get(comboKey), 4.0D, bucketMinMap.get(currentBucket), bucketMaxMap.get(currentBucket));
               turnBucketMap.put(comboKey, (currentBucket << 2) + subBucket);
            }
         }
      }

      try {
         ObjectOutputStream out;
         String sd = ""; if (AnalysisPanel.gameType == 3) {sd = "SD";}
         (out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new util.AppFile(pathPrefix + "mmnestedturn"+sd+"." + equityBuckets + ".4"))))).writeObject(turnBucketMap);
         out.close();
      } catch (Exception ignored) {
      }
   }

   private static int computeBucketIndex(double value, double bucketCount, double rangeMin, double rangeMax) {
      double range = rangeMax - rangeMin + 1.0E-8D;

      for(int bucketIter = 1; (double)bucketIter <= bucketCount; ++bucketIter) {
         if (value <= rangeMin + range * ((double)bucketIter / bucketCount)) {
            return bucketIter - 1;
         }
      }

      System.lineSeparator();
      System.lineSeparator();
      System.exit(0);
      return -1;
   }

   public static void generateFlopBuckets(int equityBuckets, int unused) throws Throwable {
      List allFlops;
      if (equityBuckets == 0) {
         TLongIntHashMap flopBucketMap = new TLongIntHashMap();

         if (AnalysisPanel.gameType == 3) {
        	 allFlops = CardArrays.dShortdeck(new card[0]);
         } else {
        	 allFlops = CardArrays.getAllCanonicalFlops(new card[0]);
         }

         int[] suitScratch = new int[4];
         card[] flopScratch = new card[5];

         for(int flopInit = 0; flopInit < 5; ++flopInit) {
            flopScratch[flopInit] = new card(0, 0);
         }

         Iterator flopIter = allFlops.iterator();

         while(flopIter.hasNext()) {
            card[] flop;
            Iterator handIter;

            if (AnalysisPanel.gameType == 3) {
            	handIter = CardArrays.eShortdeck(flop = (card[])flopIter.next()).iterator();
            } else {
            	handIter = CardArrays.getAllHoldemHandsExcluding(flop = (card[])flopIter.next()).iterator();
            }

            while(handIter.hasNext()) {
               flopScratch = (card[])handIter.next();
               long comboKey = CardCombinations.computeFlopHandHash(new card[]{flopScratch[0], flopScratch[1], flop[0], flop[1], flop[2]}, suitScratch);
               if (!flopBucketMap.containsKey(comboKey)) {
                  flopBucketMap.put(comboKey, flopBucketMap.size());
               }
            }
         }

         AnalysisPanel.isDebugMode();
         String pathPrefix = "";
         System.lineSeparator();

         try {
            Throwable suppressed = null;

            try {
            	String sd = ""; if (AnalysisPanel.gameType == 3) { sd = "SD"; }
               ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new util.AppFile(pathPrefix + "mmnestedflop"+sd+"." + 0 + ".4"))));

               try {
                  out.writeObject(flopBucketMap);
               } finally {
                  out.close();
               }

            } catch (Throwable error) {
               if (suppressed == null) {
                  suppressed = error;
               } else if (suppressed != error) {
                  suppressed.addSuppressed(error);
               }

               throw suppressed;
            }
         } catch (Throwable ignored) {
         }
      } else {
         EquityTableCache.loadEquityTables();
         EquityTableCache.loadEquity2Tables();
         if (AnalysisPanel.gameType == 3) {
        	 allFlops = CardArrays.dShortdeck(new card[0]);
         } else {
        	 allFlops = CardArrays.getAllCanonicalFlops(new card[0]);
         }
         TLongIntHashMap flopBucketMap = new TLongIntHashMap(EquityTableCache.a.size());
         TIntDoubleHashMap bucketMaxMap = new TIntDoubleHashMap();
         TIntDoubleHashMap bucketMinMap = new TIntDoubleHashMap();
         gnu.wrapper.set.LongSet visitedSet = new gnu.wrapper.set.LongSet();
         ArrayList comboKeyList = new ArrayList();
         int flopIdx = 0;
         card[] boardSlots = new card[7];
         int[] suitScratch = new int[4];

         int boardInit;
         for(boardInit = 0; boardInit < 7; ++boardInit) {
            boardSlots[boardInit] = new card(0, 0);
         }

         Iterator flopIter = allFlops.iterator();

         label763:
         while(flopIter.hasNext()) {
            card[] flop = (card[])flopIter.next();
            if (flopIdx % 10 == 0) {
               MainTabbedPane.setStatusWithProgress("Generating flop buckets.", flopIdx, allFlops.size());
            }

            ++flopIdx;
            List handList;
            if (AnalysisPanel.gameType == 0) {
            	handList = CardArrays.getAllHoldemHandsExcluding(flop);
            } else {
            	handList = CardArrays.eShortdeck(flop);
            }
            double maxEquity = 0.0D;
            double minEquity = 1.0D;
            comboKeyList.clear();

            double equityValue;
            for(Iterator handIter = handList.iterator(); handIter.hasNext(); minEquity = Math.min(minEquity, equityValue)) {
               card[] hand = (card[])handIter.next();
               long comboKey = CardCombinations.computeHandHashByLength(new card[]{hand[0], hand[1], flop[0], flop[1], flop[2]}, boardSlots, suitScratch, 5);
               comboKeyList.add(comboKey);
               equityValue = EquityTableCache.a.get(comboKey);
               maxEquity = Math.max(maxEquity, equityValue);
            }

            bucketMaxMap.clear();
            bucketMinMap.clear();
            Iterator keyIter = comboKeyList.iterator();

            while(true) {
               double comboVariance;
               do {
                  long comboKey;
                  do {
                     if (!keyIter.hasNext()) {
                        keyIter = comboKeyList.iterator();

                        while(keyIter.hasNext()) {
                           comboKey = (Long)keyIter.next();
                           if (!visitedSet.contains(comboKey)) {
                              visitedSet.add(comboKey);
                              int currentBucket = flopBucketMap.get(comboKey);
                              int subBucket = computeBucketIndex(EquityTableCache.b.get(comboKey), 4.0D, bucketMinMap.get(currentBucket), bucketMaxMap.get(currentBucket));
                              flopBucketMap.put(comboKey, (flopBucketMap.get(comboKey) << 2) + subBucket);
                           }
                        }
                        continue label763;
                     }

                     comboKey = (Long)keyIter.next();
                  } while(flopBucketMap.containsKey(comboKey));

                  double comboEquity = EquityTableCache.a.get(comboKey);
                  comboVariance = EquityTableCache.b.get(comboKey);
                  boardInit = computeBucketIndex(comboEquity, (double)equityBuckets, minEquity, maxEquity);
                  flopBucketMap.put(comboKey, boardInit);
                  if (!bucketMaxMap.containsKey(boardInit) || comboVariance > bucketMaxMap.get(boardInit)) {
                     bucketMaxMap.put(boardInit, comboVariance);
                  }
               } while(bucketMinMap.containsKey(boardInit) && comboVariance >= bucketMinMap.get(boardInit));

               bucketMinMap.put(boardInit, comboVariance);
            }
         }

         String sd = ""; if (AnalysisPanel.gameType == 3) { sd = "SD";}
         String outputName = "mmnestedflop"+sd+"." + equityBuckets + ".4";
         AnalysisPanel.isDebugMode();
         util.AppFile outputFile = new util.AppFile(outputName);

         try {
            Throwable suppressed = null;

            try {
               ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));

               try {
                  out.writeObject(flopBucketMap);
               } finally {
                  out.close();
               }

            } catch (Throwable error) {
               if (suppressed == null) {
                  suppressed = error;
               } else if (suppressed != error) {
                  suppressed.addSuppressed(error);
               }

               throw suppressed;
            }
         } catch (Throwable ignored) {
         }
      }
   }

   public static final Object readObject(String fileName) {
      return readObject((File)(new util.AppFile(fileName)));
   }

   public static final Object readObject(File dir, String fileName) {
      return readObject(new File(dir, fileName));
   }

   public static final Object readObjectFromZip(ZipFile zipFile, String entryName, String password) throws IOException {
      return readObject((InputStream)(new ZipEntryInputStream(zipFile, entryName, password)));
   }

   public static final Object readObject(InputStream inputStream) {
      try {
         Throwable suppressed = null;

         try {
            ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(inputStream, 65536));

            Object result;
            try {
               result = objectIn.readObject();
            } finally {
               objectIn.close();
            }

            return result;
         } catch (Throwable error) {
            if (suppressed == null) {
               suppressed = error;
            } else if (suppressed != error) {
               suppressed.addSuppressed(error);
            }

            throw suppressed;
         }
      } catch (Throwable ignored) {
         return null;
      }
   }

   public static final Object readObjectFromZipEntry(InputStream inputStream, String password, long compressedSize) {
      try {
         Throwable suppressed = null;

         try {
            ObjectInputStream objectIn = new ObjectInputStream(new ZipEntryInputStream(inputStream, password, compressedSize));

            Object result;
            try {
               result = objectIn.readObject();
            } finally {
               objectIn.close();
            }

            return result;
         } catch (Throwable error) {
            if (suppressed == null) {
               suppressed = error;
            } else if (suppressed != error) {
               suppressed.addSuppressed(error);
            }

            throw suppressed;
         }
      } catch (Throwable ignored) {
         return null;
      }
   }

   public static final Object readObject(File file) {
      Object compressed;
      if ((compressed = readCompressedObject(file)) != null) {
         return compressed;
      } else {
         try {
            Throwable suppressed = null;

            try {
               ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(new HandRangeWriter(file), 65536));

               Object result;
               try {
                  Object value = objectIn.readObject();
                  System.lineSeparator();
                  result = value;
               } finally {
                  objectIn.close();
               }

               return result;
            } catch (Throwable error) {
               if (suppressed == null) {
                  suppressed = error;
               } else if (suppressed != error) {
                  suppressed.addSuppressed(error);
               }

               throw suppressed;
            }
         } catch (Throwable ignored) {
            return null;
         }
      }
   }

   public static final void writeObject(File dir, String fileName, Object value) throws Throwable {
      writeObject(new File(dir, fileName), value);
   }

   public static final void writeObject(OutputStream outputStream, Object value) throws Throwable {
      try {
         Throwable suppressed = null;

         try {
            ObjectOutputStream objectOut = new ObjectOutputStream(new BufferedOutputStream(outputStream, 65536));

            try {
               objectOut.writeObject(value);
            } finally {
               objectOut.close();
            }

         } catch (Throwable error) {
            if (suppressed == null) {
               suppressed = error;
            } else if (suppressed != error) {
               suppressed.addSuppressed(error);
            }

            throw suppressed;
         }
      } catch (Throwable error) {
         throw error;
      }
   }

   private static double[] readDoubleArray(DataInputStream dataIn) throws IOException {
      int length;
      if ((length = dataIn.readInt()) < 0) {
         return null;
      } else {
         double[] result = new double[length];

         for(int idx = 0; idx < length; ++idx) {
            result[idx] = dataIn.readDouble();
         }

         return result;
      }
   }

   public static final double[][] readDoubleMatrix(InputStream inputStream) {
      try {
         Throwable suppressed = null;

         try {
            DataInputStream dataIn = new DataInputStream(new BufferedInputStream(inputStream, 65536));

            try {
               double[][] matrix = new double[dataIn.readInt()][];

               for(int row = 0; row < matrix.length; ++row) {
                  matrix[row] = readDoubleArray(dataIn);
               }

               double[][] result = matrix;
               return result;
            } finally {
               dataIn.close();
            }
         } catch (Throwable error) {
            if (suppressed == null) {
               suppressed = error;
            } else if (suppressed != error) {
               suppressed.addSuppressed(error);
            }

            throw suppressed;
         }
      } catch (Throwable error) {
         error.printStackTrace();
         return null;
      }
   }

   public static final void writeObject(File file, Object value) throws Throwable {
      if (file.getParentFile() != null) {
         file.getParentFile().mkdirs();
      }

      try {
         Throwable suppressed = null;

         try {
            ObjectOutputStream objectOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file), 65536));

            try {
               objectOut.writeObject(value);
            } finally {
               objectOut.close();
            }

         } catch (Throwable error) {
            if (suppressed == null) {
               suppressed = error;
            } else if (suppressed != error) {
               suppressed.addSuppressed(error);
            }

            throw suppressed;
         }
      } catch (Throwable error) {
         throw error;
      }
   }

   public static final void writeCompressedObject(File file, Object value) {
      if (file.getParentFile() != null) {
         file.getParentFile().mkdirs();
      }

      try {
         Throwable suppressed = null;

         try {
            ObjectOutputStream objectOut = new ObjectOutputStream(new DeflaterOutputStream(new FileOutputStream(file), new Deflater(1), 65536));

            try {
               objectOut.writeObject(value);
               objectOut.close();
            } finally {
               objectOut.close();
            }

         } catch (Throwable error) {
            if (suppressed == null) {
               suppressed = error;
            } else if (suppressed != error) {
               suppressed.addSuppressed(error);
            }

            throw suppressed;
         }
      } catch (Throwable error) {
         error.printStackTrace();
      }
   }

   public static final Object readCompressedObject(InputStream inputStream) {
      try {
         Throwable suppressed = null;

         try {
            ObjectInputStream objectIn = new ObjectInputStream(new InflaterInputStream(inputStream));

            Object result;
            try {
               result = objectIn.readObject();
            } finally {
               objectIn.close();
            }

            return result;
         } catch (Throwable error) {
            if (suppressed == null) {
               suppressed = error;
            } else if (suppressed != error) {
               suppressed.addSuppressed(error);
            }

            throw suppressed;
         }
      } catch (Throwable ignored) {
         return null;
      }
   }

   private static Object readCompressedObject(File file) {
      try {
         Throwable suppressed = null;

         try {
            ObjectInputStream objectIn = new ObjectInputStream(new InflaterInputStream(new HandRangeWriter(file)));

            Object result;
            try {
               result = objectIn.readObject();
            } finally {
               objectIn.close();
            }

            return result;
         } catch (Throwable error) {
            if (suppressed == null) {
               suppressed = error;
            } else if (suppressed != error) {
               suppressed.addSuppressed(error);
            }

            throw suppressed;
         }
      } catch (Throwable ignored) {
         return null;
      }
   }

   public static int[] flattenIntMatrix(int[][] matrix) {
      int[] flat = new int[matrix.length * matrix[0].length];

      for(int row = 0; row < matrix.length; ++row) {
         for(int col = 0; col < matrix[0].length; ++col) {
            flat[(row << 2) + col] = matrix[row][col];
         }
      }

      return flat;
   }
}
