(ns status-im.ui.screens.add-new.new-chat.db
  (:require [cljs.spec.alpha :as spec]))

(defn own-public-key?
  [{:keys [multiaccount]} public-key]
  (= (:public-key multiaccount) public-key))

(defn validate-pub-key [db public-key]
  (or
   (not (spec/valid? :global/public-key public-key))
   (own-public-key? db public-key)))