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

/** The feature statistics for a single dataset. */
case class DatasetFeatureStatistics
(
  /** The name of the dataset. */
  name: String,
  /** The number of examples in the dataset. */
  numExamples: Long,
  /** Only valid if the weight feature was specified. Treats a missing weighted feature as zero. */
  weightedNumExamples: Double,
  /** The feature statistics for the dataset. */
  features: Seq[FeatureNameStatistics]
)

/** The complete set of statistics for a given feature name for a dataset. */
case class FeatureNameStatistics
(
  /** The feature name. */
  name: String,
  /** The data type of the feature. */
  statisticsType: FeatureNameStatistics.StatisticsType.StatisticsType,
  /** The statistics of the numeric values of the feature. */
  numStats: Option[NumericStatistics],
  /** The statistics of the string values of the feature. */
  stringStats: Option[StringStatistics]
)

object FeatureNameStatistics {
  /** The types supported by the feature statistics. */
  object StatisticsType extends Enumeration {
    type StatisticsType = Value
    val Numeric, String = Value
  }
}

// ============================================================
// Numeric Statistics
// ============================================================

/** Statistics for a numeric feature in a dataset. */
case class NumericStatistics
(
  commonStats: CommonStatistics,
  /** The mean of the values. */
  mean: Double,
  /** The standard deviation of the values. */
  stdDev: Double,
  /** The number of values that equal 0. */
  numZeros: Long,
  /** The minimum value. */
  min: Double,
  /** The approximate median value. */
  approxMedian: Double,
  /** The maximum value. */
  max: Double,
  /** The histogram(s) of the feature values. */
  histograms: Seq[Histogram]
  // TODO: weighted numeric statistics
)

// ============================================================
// String Statistics
// ============================================================

case class StringStatistics
(
  commonStats: CommonStatistics,
  /** The approximate number of unique values. */
  approxUnique: Long,
  /**
   * An approximate sorted list of the most-frequent values and their frequencies, with the
   * most-frequent being first.
   */
  approxTopValues: Seq[(String, Double)],
  /** The average length of the values. */
  avgLength: Double
  // TODO: rank histogram
  // TODO: weighted string statistics
)

// ============================================================
// Histogram
// ============================================================

/** Common statistics for all feature types. */
case class CommonStatistics
(
  /**
   * The number of examples with at least one value for this feature, i.e. the number of `Some(a)`s
   * for `Option[A]`.
   */
  numNonMissing: Long,
  /**
   * The number of examples with no values for this feature, i.e. the number of `None`s for
   * `Option[A]`.
   */
  numMissing: Long,
  /**
   * The minimum number of values in a single example for this feature, i.e. `a.length` if `A` is
   * `Traversable`, otherwise 1 for `Some(a)` and 0 for `None`.
   */
  minNumValues: Long,
  /**
   * The maximum number of values in a single example for this feature, i.e. `a.length` if `A` is
   * `Traversable`, otherwise 1 for `Some(a)` and 0 for `None`.
   */
  maxNumValues: Long,
  /**
   * The average number of values in a single example for this feature, i.e. `a.length` if `A` is
   * `Traversable`, otherwise 1 for `Some(a)` and 0 for `None`.
   */
  avgNumValues: Long,
  /**
   * This is calculated directly, so should have less numerical error.
   * `totalNumValues = avgNumValues * numNonMissing`.
   */
  totalNumValues: Long,
  /** The quantiles histogram for the number of values in this feature. */
  numValuesHistogram: Histogram
  // TODO: weighted common stats
  // TODO: feature list length histogram
)

// ============================================================
// Histogram
// ============================================================

/**
 * The data used to create a histogram of a numeric feature for a dataset.
 */
case class Histogram
(
  /** The number of NaN values in the dataset. */
  numNaN: Long,
  /** The number of undefined values in the dataset. */
  numUndefined: Long,
  /** A list of buckets in the histogram, sorted from lowest bucket to highest bucket. */
  buckets: Seq[Histogram.Bucket],
  /** The type of the histogram. */
  histogramType: Histogram.HistogramType.HistogramType,
  /** An optional descriptive name of the histogram, to be used for labeling. */
  name: String
)

object Histogram {
  /**
   * Each bucket defines its low and high values along with its count. The low and high values must
   * be a real number or positive or negative infinity. They cannot be NaN or undefined. Counts of
   * those special values can be found in the numNaN and numUndefined fields.
   */
  case class Bucket
  (
    /** The low value of the bucket, inclusive. */
    lowValue: Double,
    /** The high value of the bucket, exclusive (unless the high value is positive infinity). */
    highValue: Double,
    /**
     * The number of items in the bucket. Stored as a double to be able to handle weighted
     * histograms.
     */
    sampleCount: Double
  )

  /**
   * The type of the histogram. A standard histogram has equal-width buckets. The quantiles type is
   * used for when the histogram message is used to store quantile information (by using
   * equal-count buckets with variable widths).
   */
  object HistogramType extends Enumeration {
    type HistogramType = Value
    val Standard, Quantiles = Value
  }
}
