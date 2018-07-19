/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.processors.textmining.flink.processor.language;

import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LanguageDetection implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

  private static final String LANGUAGE_KEY = "language";

  private String fieldName;
  private LanguageDetector languageDetector;
  private TextObjectFactory textObjectFactory;

  public LanguageDetection(String fieldName) {
    this.fieldName = fieldName;
    List<LanguageProfile> languageProfiles = null;
    try {
      languageProfiles = new LanguageProfileReader().readAllBuiltIn();
    } catch (IOException e) {
      e.printStackTrace();
    }

    this.languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
            .withProfiles(languageProfiles)
            .build();

    this.textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
  }

  @Override
  public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out)  {

    TextObject textObject = textObjectFactory.forText(String.valueOf(in.get(fieldName)));
    com.google.common.base.Optional<LdLocale> lang = languageDetector.detect(textObject);

    if (lang.isPresent()) {
      in.put(LANGUAGE_KEY, lang.get().getLanguage());
    } else {
      in.put(LANGUAGE_KEY, "unknown");
    }

    out.collect(in);
  }
}
