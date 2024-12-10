(ns status-im.contexts.keycard.feature-unavailable.events
  (:require
    [status-im.contexts.keycard.feature-unavailable.view :as feature-unavailable]
    [utils.re-frame :as rf]))

(rf/reg-event-fx
 :keycard/feature-unavailable-show
 (fn [_ [options]]
   {:fx [[:dispatch
          [:show-bottom-sheet
           {:theme   (:theme options)
            :content feature-unavailable/view}]]]}))
