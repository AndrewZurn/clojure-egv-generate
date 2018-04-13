(ns my-api.egv-generator
  (:require [compojure.api.sweet :refer :all])
  (:import (java.time LocalDateTime)))

(defn- next-date-time [day-offset minute-offset]
  (-> (LocalDateTime/now)
      (.plusDays day-offset)
      (.plusMinutes minute-offset)))

(def is-odd-day? (fn [day-offset] (= 0 (mod day-offset 2))))

(defn- gen-next-egv [day-offset iteration]
  (let [minute-offset (* 5 iteration)
        system-time (next-date-time day-offset minute-offset)
        value-modifier (if (is-odd-day? day-offset) + -)
        starting-value (if (is-odd-day? day-offset) 100 388)]
    {:system-time       (str system-time)
     :display-time      (str (.plusMinutes system-time 480)) ;; add 8 hours
     :value             (value-modifier starting-value iteration)
     :trend             (if (is-odd-day? day-offset) "forty-five-up" "forty-five-down")
     :trend-rate        (if (is-odd-day? day-offset) 1.0 -1.0)
     :serial-number     "ABC12345"
     :transmitter-id    "6453314"
     :transmitter-ticks (+ (System/currentTimeMillis)
                           (long (* (+ 1 day-offset) minute-offset 60 1000)))}))

(defn- generate-next-day-egvs [day-offset]
  (map (fn [i] (gen-next-egv day-offset i)) (range 0 288)))

(defn generate-all-egvs [num-days]
  (flatten (map generate-next-day-egvs (range 0 num-days))))
