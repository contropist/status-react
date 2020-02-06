(ns status-im.ui.screens.keycard.components.animated-circles 
  (:require [status-im.ui.components.react :as react]
            [reagent.core :as reagent]
            [status-im.ui.components.icons.vector-icons :as vector-icons]
            [status-im.ui.components.animation :as animation]

            [status-im.react-native.resources :as resources]
            [status-im.ui.components.colors :as colors]))

(defn circle [{:keys [animation-value color size]}]
  [react/animated-view
   {:style {:width            size
            :height           size
            :position         "absolute"
            :background-color color
            :border-radius    (/ size 2)
            :opacity          (animation/interpolate
                               animation-value
                               {:inputRange [0 1 2]
                                :outputRange [0.7 1 0]})
            :transform        [{:scale (animation/interpolate
                                        animation-value
                                        {:inputRange  [0 1]
                                         :outputRange [0.9 1]})}]}}])


(defn animate-card-position [card-scale animation-value]
  {:transform [{:scale card-scale}
               {:translateX (animation/x animation-value)}
               {:translateY (animation/y animation-value)}]})

(defn card [{:keys [card-color
                    chip-color
                    key-color
                    card-scale
                    animation-value]}]
  [react/animated-view
   {:style (merge
            (animate-card-position card-scale animation-value)
            {:height           76
             :width            114
             :border-radius    16
             :position         :absolute
             :shadow-offset    {:width 0 :height 2}
             :shadow-radius    16
             :elevation        8
             :shadow-opacity   0.1
             :shadow-color     "gba(0, 9, 26, 0.12)"
             :background-color card-color})}
   [react/animated-view
    {:style {:width            16
             :height           12
             :border-radius    2
             :left             "10%"
             :top              "30%"
             :background-color chip-color}}]
   [react/view
    {:style {:position        :absolute
             :justify-content :center
             :top             10
             :right           20}}
    [vector-icons/icon :main-icons/keycard-logo
     {:color  key-color
      :height 42}]]])

(defn phone [{:keys [animation-value]}]
  [react/animated-view {:style {:opacity   (animation/interpolate
                                            animation-value
                                            {:inputRange  [0 1]
                                             :outputRange [0 0.9]})
                                :position  :absolute
                                :bottom    0
                                :transform [{:translateY
                                             (animation/interpolate
                                              animation-value
                                              {:inputRange  [0 1]
                                               :outputRange [125 10]})}]}}
   [react/image {:source (resources/get-image :onboarding-phone)
                 :style  {:height 125
                          :width  86}}]])

(defn indicator-container [anim children]
  [react/animated-view
   {:style {:position         "absolute"
            :justify-content  :center
            :align-items      :center
            :border-radius    21
            :width            42
            :height           42
            :left             41
            :shadow-offset    {:width 0 :height 2}
            :shadow-radius    16
            :elevation        8
            :shadow-opacity   0.1
            :shadow-color     "gba(0, 9, 26, 0.12)"
            :background-color :white
            :transform        [{:scale (animation/interpolate
                                        anim
                                        {:inputRange  [0 1]
                                         :outputRange [0 1]})}]}}
   children])

(defn indicator [{:keys [state animation-value]}]
  (case @state
    :error
    [indicator-container animation-value
     [vector-icons/icon :main-icons/close {:color colors/red}]]
    :success
    [indicator-container animation-value
     [vector-icons/icon :main-icons/check {:color colors/green}]]
    :connected
    [indicator-container animation-value
     [vector-icons/icon :main-icons/check {:color colors/blue}]]
    :processing
    [indicator-container animation-value
     [react/activity-indicator {:color colors/blue}]]
    nil))

(def circle-easing    (animation/bezier 0.455 0.03 0.515 0.955))
(def card-easing      (animation/bezier 0.77 0 0.175 1))

(defn start-animation
  [{:keys [small medium big card card-scale phone active-animations]}]
  (let [phone-enter-at   8000
        circle-animation #(animation/timing %1 {:toValue  %2
                                                :delay    %3
                                                :duration 1000
                                                :easing   circle-easing})
        card-loop        (animation/anim-loop
                          (animation/anim-sequence
                           [(animation/timing card
                                              {:toValue  #js {:x 70
                                                              :y 10}
                                               :duration 1000
                                               :delay    2000
                                               :easing   card-easing})
                            (animation/timing card
                                              {:toValue  {:x -70
                                                          :y 120}
                                               :duration 1000
                                               :delay    2000
                                               :easing   card-easing})
                            (animation/timing card
                                              {:toValue  #js {:x -70
                                                              :y 10}
                                               :duration 1000
                                               :delay    2000
                                               :easing   card-easing})
                            (animation/timing card
                                              {:toValue  #js {:x 70
                                                              :y 120}
                                               :duration 1000
                                               :delay    2000
                                               :easing   card-easing})]))
        circles          (animation/anim-loop
                          (animation/parallel
                           [(animation/anim-sequence
                             [(circle-animation small 1 0)
                              (circle-animation small 0 0)])
                            (animation/anim-sequence
                             [(circle-animation medium 1 200)
                              (circle-animation medium 0 0)])
                            (animation/anim-sequence
                             [(circle-animation big 1 400)
                              (circle-animation big 0 0)])]))
        phone-entrace    (animation/anim-sequence
                          [(animation/anim-delay phone-enter-at)
                           (animation/parallel
                            [(animation/timing phone
                                               {:toValue  1
                                                :duration 1000
                                                :delay    2000
                                                :easing   card-easing})
                             (animation/timing card-scale
                                               {:toValue  0.5
                                                :duration 1000
                                                :delay    2000
                                                :easing   card-easing})
                             card-loop])])
        animation        (animation/parallel
                          [circles
                           phone-entrace])]
    (swap! active-animations conj animation)
    (animation/start animation)))

(defn on-error [{:keys [state]}]
  (reset! state :error))

(defn on-processing [{:keys [state]}]
  (reset! state :processing))

(defn on-success [{:keys [state]}]
  (reset! state :success))

(defn on-connect [{:keys [state
                          active-animations
                          card
                          small
                          indicator
                          medium
                          big
                          card-scale
                          phone]}]
  (reset! state :connected)
  (for [v @active-animations]
    (animation/stop-animation v))
  (animation/start
    (animation/parallel
     [(animation/timing card-scale
                        {:toValue 1
                         :timing  1000
                         :easing  card-easing})
      (animation/timing indicator
                        {:toValue 1
                         :timing  1000
                         :easing  card-easing})
      (animation/timing small
                        {:toValue 2
                         :timing  1000
                         :easing  circle-easing})
      (animation/timing medium
                        {:toValue 2
                         :timing  1000
                         :easing  circle-easing})
      (animation/timing big
                        {:toValue 2
                         :timing  1000
                         :easing  circle-easing})
      (animation/timing phone
                        {:toValue 0
                         :timing  1000
                         :easing  card-easing})
      (animation/timing card
                        {:toValue #js {:x 0
                                       :y 0}
                         :timing  3000
                         :easing  card-easing})])))

(defn card-colors [state]
  (case state
    ( :init :awaiting)
    {:card-color "#2D2D2D"
     :key-color  "#27D8B9"
     :chip-color "#F0CC73"}
    (:connected :processing)
    {:card-color colors/blue
     :key-color  :white
     :chip-color :white}
    :success
    {:card-color colors/green
     :key-color  :white
     :chip-color :white}
    :error
    {:card-color colors/red
     :key-color  :white
     :chip-color :white}))

(defn animated-circles []
  (let [animation-small     (animation/create-value 0)
        animation-medium    (animation/create-value 0)
        animation-big       (animation/create-value 0)
        animation-card      (animation/create-value-xy #js {:x 0
                                                            :y 0})
        card-scale          (animation/create-value 0.7)
        animation-phone     (animation/create-value 0)
        animation-indicator (animation/create-value 0)
        active-animations   (reagent/atom nil)
        state               (reagent/atom :init)]
    (js/setTimeout #(on-connect {:state             state
                                 :active-animations active-animations
                                 :indicator         animation-indicator
                                 :card              animation-card
                                 :card-scale        card-scale
                                 :phone             animation-phone
                                 :small             animation-small
                                 :medium            animation-medium
                                 :big               animation-big})
                   25000)
    (js/setTimeout #(on-processing {:state             state
                                    :active-animations active-animations
                                    :card              animation-card})
                   27000)
    (js/setTimeout #(on-success {:state             state
                                 :active-animations active-animations
                                 :card              animation-card})
                   30000)
    (js/setTimeout #(on-error {:state             state
                               :active-animations active-animations
                               :card              animation-card})
                   33000)
    (start-animation {:small             animation-small
                      :medium            animation-medium
                      :big               animation-big
                      :phone             animation-phone
                      :card              animation-card
                      :card-scale        card-scale
                      :active-animations active-animations})

    (fn []
      [react/view {:style {:position        :absolute
                           :top             0
                           :bottom          0
                           :left            0
                           :right           0
                           :justify-content :center
                           :align-items     :center}}
       [circle {:animation-value animation-big
                :size            200
                :color           "#F1F4FF"}]
       [circle {:animation-value animation-medium
                :size            140
                :color           "#E3E8FA"}]
       [circle {:animation-value animation-small
                :size            80
                :color           "#D2D9F0"}]
      [card (merge (card-colors @state)
                   {:animation-value animation-card
                    :card-scale      card-scale})]
       [phone {:animation-value animation-phone}]
       [indicator {:state           state
                   :animation-value animation-indicator}]])))
