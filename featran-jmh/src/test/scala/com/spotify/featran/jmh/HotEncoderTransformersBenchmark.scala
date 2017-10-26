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

package com.spotify.featran.jmh

import java.util.concurrent.TimeUnit

import com.spotify.featran.{FeatureExtractor, FeatureSpec}
import com.spotify.featran.transformers.{NHotEncoder, OneHotEncoder}
import org.openjdk.jmh.annotations._
import org.scalacheck._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
class HotEncoderTransformersBenchmark {

  //
  // N HOT - optional
  //
  @Benchmark
  def nHotOptionalEncoder: Unit = nHotOptional(arbitraryListString)

  @Benchmark
  def nHotOptionalEncoder1000Values: Unit =
    nHotOptional(arbitraryListString1000Values)

  //
  // N HOT - required
  //
  @Benchmark
  def nHotRequiredEncoder: Unit = nHotRequired(arbitraryListString)

  @Benchmark
  def nHotRequiredEncoder10Values: Unit =
    nHotRequired(arbitraryListString10Values)

  @Benchmark
  def nHotRequiredEncoder10SameValues: Unit =
    nHotRequired(arbitraryListString10SameValues)

  @Benchmark
  def nHotRequiredEncoder1000Values: Unit =
    nHotRequired(arbitraryListString1000Values)

  @Benchmark
  def nHotRequiredEncoder1000SameValues: Unit =
    nHotRequired(arbitraryListString1000SameValues)

  //
  // N HOT - transform optional to list
  //
  @Benchmark
  def nHotRequiredWithListMatch: Unit =
    nHotRequiredWithListMatchPrepare(arbitraryListOptional)

  @Benchmark
  def nHotRequiredWithListMatch10Values: Unit =
    nHotRequiredWithListMatchPrepare(arbitraryListOptional10Values)

  @Benchmark
  def nHotRequiredWithListMatch1000Values: Unit =
    nHotRequiredWithListMatchPrepare(arbitraryListOptional1000Values)

  //
  // ONE HOT - required
  //
  @Benchmark
  def oneHotRequiredEncoder: Unit = oneHotRequired(arbitraryString)

  @Benchmark
  def oneHotRequiredEncoder1000Values: Unit =
    oneHotRequired(arbitraryString1000Values)

  //
  // ONE HOT - optional
  //
  @Benchmark
  def oneHotOptionalEncoder: Unit = oneHotOptional(arbitraryString)

  @Benchmark
  def oneHotOptionalEncoder10Values: Unit =
    oneHotOptional(arbitraryString10Values)

  @Benchmark
  def oneHotOptionalEncoder10SameValues: Unit =
    oneHotOptional(arbitraryString10SameValues)

  @Benchmark
  def oneHotOptionalEncoder1000Values: Unit =
    oneHotOptional(arbitraryString1000Values)

  @Benchmark
  def oneHotOptionalEncoder1000SameValues: Unit =
    oneHotOptional(arbitraryString1000SameValues)

  def nHotOptional(
      input: List[List[String]]): FeatureExtractor[List, List[String]] = {
    val featureSpec = FeatureSpec
      .of[List[String]]
      .optional {
        case List("") => None
        case i        => Option(i)
      }(NHotEncoder("n_hot"))
    featureSpec.extract(input)
  }

  def nHotRequired(
      input: List[List[String]]): FeatureExtractor[List, List[String]] = {
    val featureSpec = FeatureSpec
      .of[List[String]]
      .required(identity)(NHotEncoder("n_hot"))
    featureSpec.extract(input)
  }

  def nHotRequiredWithListMatchPrepare(
      input: List[Option[String]]): FeatureExtractor[List, List[String]] = {
    val listInput = input.map {
      case Some("") => List()
      case Some(i)  => List(i)
      case _        => List()
    }
    val featureSpec = FeatureSpec
      .of[List[String]]
      .required(identity)(NHotEncoder("n_hot"))
    featureSpec.extract(listInput)
  }

  def oneHotOptional(input: List[String]): FeatureExtractor[List, String] = {
    val featureSpec = FeatureSpec
      .of[String]
      .optional {
        case "" => None
        case f  => Option(f)
      }(OneHotEncoder("one_hot"))
    featureSpec.extract(input)
  }

  def oneHotRequired(input: List[String]): FeatureExtractor[List, String] = {
    val featureSpec = FeatureSpec
      .of[String]
      .required(identity)(OneHotEncoder("one_hot"))
    featureSpec.extract(input)
  }

  private val thisString = Gen.alphaStr.toString

  // Lists of Lists for N-Hot
  private implicit val arbitraryListString: List[List[String]] = List(
    List(Gen.alphaStr.toString))
  private implicit val arbitraryListString10Values: List[List[String]] = List(
    Range(0, 10).map(_ => Gen.alphaStr.toString).toList)
  private implicit val arbitraryListString10SameValues: List[List[String]] =
    List(Range(0, 10).map(_ => thisString).toList)
  private implicit val arbitraryListString1000Values: List[List[String]] = List(
    Range(0, 1000).map(_ => Gen.alphaStr.toString).toList)
  private implicit val arbitraryListString1000SameValues: List[List[String]] =
    List(Range(0, 1000).map(_ => thisString).toList)

  // Lists for One-Hot
  private implicit val arbitraryString: List[String] = List(
    Gen.alphaStr.toString)
  private implicit val arbitraryString10Values: List[String] =
    Range(0, 10).map(_ => Gen.alphaStr.toString).toList
  private implicit val arbitraryString10SameValues: List[String] =
    Range(0, 10).map(_ => thisString).toList
  private implicit val arbitraryString1000Values: List[String] =
    Range(0, 1000).map(_ => Gen.alphaStr.toString).toList
  private implicit val arbitraryString1000SameValues: List[String] =
    Range(0, 1000).map(_ => thisString).toList

  // Lists of options for transforming to a lists of lists
  private implicit val arbitraryListOptional: List[Option[String]] =
    List(Option(Gen.alphaStr.toString))
  private implicit val arbitraryListOptional10Values: List[Option[String]] =
    Range(0, 10).map(_ => Option(Gen.alphaStr.toString)).toList
  private implicit val arbitraryListOptional1000Values: List[Option[String]] =
    Range(0, 1000).map(_ => Option(Gen.alphaStr.toString)).toList
}
