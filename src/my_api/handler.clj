(ns my-api.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [clj-http.client :as client]
            [cheshire.core :refer :all]
            [clojure.string :as string]))

(s/defschema Pizza
  {(s/required-key :name)        (s/pred (fn [str] (string/includes? (string/lower-case str) "pepperoni") 'contains-pepperoni?))
   (s/optional-key :description) s/Str
   :size                         (s/enum :L :M :S)
   :origin                       {:country (s/enum :FI :PO)
                                  :city    s/Str}})

(defn handle-req [req-fn]
  "Expects a request function that returns a map that describes the http response"
  (let [start (System/currentTimeMillis)]
    (assoc (req-fn) :headers {"request-time" (str (- (System/currentTimeMillis) start) "ms")})))

(def app
  (api
    {:swagger
     {:ui   "/"
      :spec "/swagger.json"
      :data {:info {:title       "My-api"
                    :description "Compojure Api example"}
             :tags [{:name "api", :description "some apis"}]}}}

    (context "/api" []
      :tags ["api"]

      (GET "/plus" []
        :return {:result Long}
        :query-params [x :- Long, y :- Long]
        :summary "adds two numbers together"
        (ok {:result (+ x y)}))

      (POST "/echo" []
        :return Pizza
        :body [pizza Pizza]
        :summary "echoes a Pizza"
        (ok pizza))

      (GET "/me" []
        :return {:message String}
        :query-params [name :- String]
        :summary "returns your name"
        (ok {:message (str "Hello, and goodbye " name)})))

    (GET "/call" []
      :summary "returns some json"
      (handle-req
        (fn []
          (let [dexcom-resp (parse-string (:body (client/get "https://api.dexcom.com/info")))
                post-resp (parse-string (:body (client/get "https://jsonplaceholder.typicode.com/posts/1")))]
            (ok (conj dexcom-resp post-resp))))))))

(defn something [parts]
  (println (str "parts are" parts))
  (loop [remaining-parts parts
         final-parts []]
    (println (str "parts are" parts))
    (println (str "remaining-parts are" remaining-parts))
    (println (str "final-parts are" final-parts))
    (if (empty? remaining-parts)
      final-parts
      (let [[ps & remaining] remaining-parts]
        (recur remaining
               (conj final-parts ps))))))

(reduce (fn [accum next] {:prev next
                          :accum (+ (:accum accum) next)})
        {:prev 0 :accum 0}
        [1 2 3 5 8 13 21])