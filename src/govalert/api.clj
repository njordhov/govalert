(ns govalert.api
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [ring.adapter.jetty :refer (run-jetty)]
            [ring.util.response :as resp]
            [environ.core :refer [env]]
            [govalert.elastic.db :as db :refer (with-index)]
            [govalert.elastic.store :as elastic]))

(defroutes app-routes
  (GET "/" [] 
    (resp/redirect "/index.html"))
  (POST "/subscribe" [govbody email query]
    (with-index govbody
      (elastic/add-subscription email query)))
  (route/resources "/")
  (route/not-found "Not Found"))

(defn start-server [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (run-jetty (site app-routes) {:port port :join? false})))

(defn -main [es-endpoint & [port]]
  (assert es-endpoint)
  (db/init es-endpoint)
  (start-server port))

