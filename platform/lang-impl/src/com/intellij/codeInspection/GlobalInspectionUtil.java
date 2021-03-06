/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.codeInspection;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.reference.RefElement;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Maxim.Mossienko
 * Date: 16.09.2009
 * Time: 20:35:06
 */
public class GlobalInspectionUtil {
  private static final String LOC_MARKER = " #loc";

  public static RefElement retrieveRefElement(PsiElement element, GlobalInspectionContext globalContext) {
    PsiFile elementFile = element.getContainingFile();
    RefElement refElement = globalContext.getRefManager().getReference(elementFile);
    if (refElement == null) {
      PsiElement context = InjectedLanguageManager.getInstance(elementFile.getProject()).getInjectionHost(elementFile);
      if (context != null) refElement = globalContext.getRefManager().getReference(context.getContainingFile());
    }
    return refElement;
  }

  public static String createInspectionMessage(String message) {
    //TODO: FIXME!
    return message + LOC_MARKER;
  }

  public static void createProblem(PsiElement elt, HighlightInfo info, TextRange range,
                                   @Nullable String problemGroup,
                                   InspectionManager manager, ProblemDescriptionsProcessor problemDescriptionsProcessor,
                                   GlobalInspectionContext globalContext) {
    List<LocalQuickFix> fixes = new ArrayList<LocalQuickFix>();
    if (info.quickFixActionRanges != null) {
      for (Pair<HighlightInfo.IntentionActionDescriptor, TextRange> actionRange : info.quickFixActionRanges) {
        final IntentionAction action = actionRange.getFirst().getAction();
        if (action instanceof LocalQuickFix) {
          fixes.add((LocalQuickFix)action);
        }
      }
    }
    ProblemDescriptor descriptor = manager.createProblemDescriptor(elt, range, createInspectionMessage(info.description),
                                                                   HighlightInfo.convertType(info.type), false,
                                                                   fixes.isEmpty() ? null : fixes.toArray(new LocalQuickFix[fixes.size()]));
    descriptor.setProblemGroup(problemGroup);
    problemDescriptionsProcessor.addProblemElement(
      retrieveRefElement(elt, globalContext),
      descriptor
    );
  }
}
