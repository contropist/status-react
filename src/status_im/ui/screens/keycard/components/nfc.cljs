(ns status-im.ui.screens.keycard.components.nfc
  (:require [re-frame.core :as re-frame]
            [status-im.i18n :as i18n]
            [status-im.ui.components.icons.vector-icons :as vector-icons]
            [status-im.ui.components.react :as react]
            [status-im.ui.screens.keycard.components.animated-circles
             :refer [animated-circles]]
            [status-im.react-native.resources :as resources]
            [status-im.ui.components.colors :as colors]
            [status-im.ui.components.button :as button]
            [status-im.ui.screens.keycard.styles :as styles]))

(def wrapper-style {:flex            1
                    :align-items     :center
                    :justify-content :center})

(def container-style {:flex-direction  :column
                      :align-items     :center
                      :padding-horizontal 40})

(def helper-text-style {:text-align :center
                        :color colors/gray
                        :line-height 22})

(defn nfc-on []
  [react/view {:style wrapper-style}
   [react/view {:style container-style}
    [vector-icons/icon :main-icons/union-nfc {:color  colors/blue
                                              :height 36
                                              :width  36}]
    [react/view {:margin-top 16}
     [react/text {:style {:typography :title-bold}}
      (i18n/label :t/turn-nfc-on)]]
    [react/view {:margin-top 8}
     [react/text {:number-of-lines 2
                  :style           helper-text-style}
      (i18n/label :t/turn-nfc-description)]]

    [button/button {:label    :t/open-nfc-settings
                    :style    {:margin-top 16}
                    :on-press #(re-frame/dispatch [:keycard.onboarding.nfc-on/open-nfc-settings-pressed])}]]])

(defn looking-for-card []
  [react/view {:style container-style}
   [react/view {:height 200
                :margin-bottom 20}
    [animated-circles]]
   ;; [react/text {:style {:line-height 22}}
   ;;  "Looking for card.."]
   ;; [react/text {:style           helper-text-style
   ;;              :number-of-lines 2}
   ;;  "Put the card to the back of your phone to continue"]
   ])

(defn still-looking-for-card []
  [react/view {:style container-style}
   [react/view {:height 200}
    [animated-circles]]
   [react/text {:style {:line-height 22}}
    "Still looking.."]
   [react/text {:style           helper-text-style
                :number-of-lines 2}
    "Try moving the card around to find the NFC reader on your device"]])

(defn test-sheet []
  [looking-for-card])
