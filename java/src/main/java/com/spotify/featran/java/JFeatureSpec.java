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

package com.spotify.featran.java;

import com.spotify.featran.CollectionType;
import com.spotify.featran.FeatureSpec;
import com.spotify.featran.transformers.Transformer;
import scala.Function1;
import scala.Option;

import java.util.List;
import java.util.Optional;

/**
 * Java wrapper for {{@link FeatureSpec}}.
 *
 * Note that {{@link List}} is the only supported input collection type.
 */
public class JFeatureSpec<T> {

  private final FeatureSpec<T> self;

  private JFeatureSpec(FeatureSpec<T> self) {
    this.self = self;
  }

  /**
   * Create a new {{@link JFeatureSpec}} instance.
   */
  public static <T> JFeatureSpec<T> create() {
    return wrap(FeatureSpec.<T>of());
  }

  /**
   * Wrap an existing {{@link FeatureSpec}} instance.
   */
  public static <T> JFeatureSpec<T> wrap(FeatureSpec<T> self) {
    return new JFeatureSpec<>(self);
  }

  /**
   * Java wrapper for {{@link FeatureSpec#required(Function1, Transformer)}}.
   */
  public <A> JFeatureSpec<T> required(final SerializableFunction<T, A> f,
                                      final Transformer<A, ?, ?> t) {
    Function1<T, A> g = JavaOps.requiredFn(f);
    return wrap(self.required(g, t));
  }

  /**
   * Java wrapper for {{@link FeatureSpec#optional(Function1, Transformer)}}.
   */
  public <A> JFeatureSpec<T> optional(final SerializableFunction<T, Optional<A>> f,
                                      final Transformer<A, ?, ?> t) {
    Function1<T, Option<A>> g = JavaOps.optionalFn(f);
    Option<A> o = Option.empty();
    return wrap(self.optional(g, o, t));
  }

  /**
   * Java wrapper for {{@link FeatureSpec#optional(Function1, Transformer)}}.
   */
  public <A> JFeatureSpec<T> optional(final SerializableFunction<T, Optional<A>> f,
                                      final A defaultValue,
                                      final Transformer<A, ?, ?> t) {
    Function1<T, Option<A>> g = JavaOps.optionalFn(f);
    Option<A> o = Option.apply(defaultValue);
    return wrap(self.optional(g, o, t));
  }

  /**
   * Java wrapper for {{@link FeatureSpec#extract(Object, CollectionType)}}.
   */
  public JFeatureExtractor<T> extract(List<T> input) {
    return new JFeatureExtractor<>(JavaOps.extract(self, input));
  }

  /**
   * Java wrapper for {{@link FeatureSpec#extractWithSettings(Object, Object, CollectionType)}.
   */
  public JFeatureExtractor<T> extractWithSettings(List<T> input, String settings) {
    return new JFeatureExtractor<>(JavaOps.extractWithSettings(self, input, settings));
  }

}
