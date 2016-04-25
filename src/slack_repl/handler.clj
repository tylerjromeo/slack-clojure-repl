(ns slack-repl.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def settings
  (assoc-in site-defaults
    [:security :anti-forgery] false))

(defroutes app-routes
  (POST "/" [name] (str "Hello " name))
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes settings))
