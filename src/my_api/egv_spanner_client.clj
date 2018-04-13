(ns my-api.egv-spanner-client
  (:require [my-api.egv-generator :as egvs])
  (:import (com.google.cloud.spanner DatabaseId
                                     SpannerOptions
                                     Mutation)
           (java.time LocalDateTime ZoneOffset)
           (java.util Date)
           (com.google.cloud Timestamp)))

(def ^:private patient-id "c2c798cb-8e1f-4bf9-a898-ddde7c093138")
(def ^:private instance-id (System/getenv "POC_SPANNER_INSTANCE_ID"))
(def ^:private database-id (System/getenv "POC_SPANNER_DATABASE_ID"))
(def ^:private spanner-options (.build (SpannerOptions/newBuilder)))
(def ^:private spanner-service (.getService spanner-options))

(def create-db-client
  (let [database-id (DatabaseId/of (.getProjectId spanner-options)
                                   instance-id
                                   database-id)]
    (.getDatabaseClient spanner-service database-id)))

(defn close-spanner-service [] (.close spanner-service))

(defn to-timestamp [date-as-str]
  (-> (LocalDateTime/parse date-as-str)
      (.toInstant ZoneOffset/UTC)
      (Date/from)
      (Timestamp/of)))

(defn create-mutations [generated-egvs]
  (map (fn [egv]
         (-> (Mutation/newInsertBuilder "egvs")
             (.set "patient_id")
             (.to patient-id)
             (.set "system_time")
             (.to (to-timestamp (:system-time egv)))
             (.set "display_time")
             (.to (to-timestamp (:display-time egv)))
             (.set "value")
             (.to (:value egv))
             (.set "trend")
             (.to  (:trend egv))
             (.set "trend_rate")
             (.to (:trend-rate egv))
             (.build)))
       generated-egvs))

(defn insert-egvs []
  (let [generated-egvs (egvs/generate-all-egvs 30)
        ;insert-statements (create-egv-insert-statment generated-egvs)
        mutation-statements-chunked (partition 3 (create-mutations generated-egvs))]
    (doseq [statements mutation-statements-chunked]
      (.write create-db-client statements))))

;;CREATE TABLE egvs (
;	patient_id STRING(MAX) NOT NULL,
;	system_time TIMESTAMP NOT NULL,
;	display_time TIMESTAMP NOT NULL,
;	trend STRING(MAX),
;	trend_rate FLOAT64,
;	value INT64 NOT NULL) PRIMARY KEY (system_time DESC, patient_id)
