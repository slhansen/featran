/*
 * Copyright 2017 Spotify AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.spotify.featran.statistics

case class CommonStats
(
  numNonMissing: Long, // # of Some for a
  numMissing: Long, // # of None for a
  minNumValues: Long, // 1 for most As, n for A = Seq, Set, Array, i.e. n-hot, vectors.
  maxNumValues: Long, // same as minNumValues,
  avgNumValues: Long, // same as above
  totalNumValues: Long, // same as above
  // histogram
)
sealed trait Stats

case class NumericStats(// common stats
                        mean: Double,
                        stdDev: Double,
                        numZeros: Long,
                        min: Double,
                        approxMedian: Double,
                        max: Double
                        // histogram
                        // weighted
                       ) extends Stats

case class ValueAndFreq(value: String, freq: Double)
case class StringStats(// common stats
                       approxUnique: Long,
                       approxTopValues: Seq[ValueAndFreq],
                       avgLength: Double,
                       // rank histogram
                      ) extends Stats

sealed trait CanBuildStats[T]