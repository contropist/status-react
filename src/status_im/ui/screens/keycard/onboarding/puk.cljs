(ns status-im.ui.screens.keycard.onboarding.puk
  (:require-macros [status-im.utils.views :refer [defview letsubs]])
  (:require [status-im.ui.components.react :as react]
            [status-im.ui.screens.keycard.styles :as styles]
            [status-im.ui.components.toolbar.view :as toolbar]
            [status-im.ui.components.colors :as colors]
            [status-im.i18n :as i18n]
            [re-frame.core :as re-frame]
            [status-im.ui.components.common.common :as components.common]
            [status-im.ui.components.styles :as components.styles]))


(defview puk-code []
  (letsubs [secrets [:hardwallet-secrets]
            steps [:hardwallet-flow-steps]
            puk-code [:hardwallet-puk-code]]
    [react/view styles/container
     [toolbar/toolbar
      {:transparent? true}
      [toolbar/nav-text
       {:handler #(re-frame/dispatch [:keycard.onboarding.ui/cancel-pressed])
        :style   {:padding-left 21}}
       (i18n/label :t/cancel)]
      [react/text {:style {:color colors/gray}}
       (i18n/label :t/step-i-of-n {:step   "2"
                                   :number steps})]]
     [react/scroll-view {:content-container-style {:flex-grow       1
                                                   :justify-content :space-between}}
      [react/view {:flex            1
                   :flex-direction  :column
                   :justify-content :space-between
                   :align-items     :center}
       [react/view {:flex-direction :column
                    :align-items    :center}
        [react/view {:margin-top 16}
         [react/text {:style {:typography :header
                              :text-align :center}}
          (i18n/label :t/keycard-onboarding-puk-code-header)]]
        [react/view {:margin-top 32
                     :width      "85%"}
         [react/view {:justify-content :center
                      :flex-direction  :row}
          [react/view {:width             "100%"
                       :margin-horizontal 16
                       :height            108
                       :align-items       :center
                       :justify-content   :space-between
                       :flex-direction    :column
                       :background-color  colors/gray-lighter
                       :border-radius     8}
           [react/view {:justify-content :center
                        :flex            1
                        :margin-top      10}
            [react/text {:style {:color      colors/gray
                                 :text-align :center}}
             (i18n/label :t/puk-code)]]
           [react/view {:justify-content :flex-start
                        :flex            1}
            [react/text {:style {:typography  :header
                                 :font-family "monospace"
                                 :text-align  :center
                                 :color       colors/blue}}
             puk-code]]]]
         [react/view {:margin-top 16}
          [react/text {:style {:color colors/gray}}
           (i18n/label :t/puk-code-explanation)]]
         [react/view {:justify-content :center
                      :margin-top      32
                      :flex-direction  :row}
          [react/view {:width             "100%"
                       :margin-horizontal 16
                       :height            108
                       :align-items       :center
                       :justify-content   :space-between
                       :flex-direction    :column
                       :background-color  colors/gray-lighter
                       :border-radius     8}
           [react/view {:justify-content :center
                        :flex            1
                        :margin-top      10}
            [react/text {:style {:color      colors/gray
                                 :text-align :center}}
             (i18n/label :t/pair-code)]]
           [react/view {:justify-content :flex-start
                        :flex            1}
            [react/text {:style {:typography  :header
                                 :text-align  :center
                                 :font-family "monospace"
                                 :color       colors/blue}}
             (:password secrets)]]]]
         [react/view {:margin-top 16}
          [react/text {:style {:color colors/gray}}
           (i18n/label :t/pair-code-explanation)]]]]
       [react/view {:flex-direction  :row
                    :justify-content :space-between
                    :align-items     :center
                    :width           "100%"
                    :height          86}
        [react/view components.styles/flex]
        [react/view {:margin-right 20}
         [components.common/bottom-button
          {:on-press #(re-frame/dispatch [:keycard.onboarding.puk-code.ui/next-pressed])
           :forward? true}]]]]]]))
