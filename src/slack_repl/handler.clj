(ns slack-repl.handler
  (:require [clojure.string :refer [index-of]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def settings
  (assoc-in site-defaults
    [:security :anti-forgery] false))

(defn strip-compile-error
  "given a runtime exception, strips out the name of the exception"
  [message]
  (let [message-index (+ (clojure.string/index-of message \:) 2)]
    (subs message message-index)))

(defn wrap-slack-message
  "wrap a slack message in json that will casue it to post publicly in the channel" ; TODO use a real json library for this
  [string]
  (str "{\"response_type\": \"in_channel\", \"text\": \"" string "\"}"))

(defn run-command
  "Evaluates a string representation of a clojure form. Returns the result or a sensible error messagesite-defaults"
  [form]
  (try
    (wrap-slack-message (str (load-string form)))
    ;TODO handle things that get println'd
    ;TODO handle infinite loops
    (catch Exception e (str "Uh-oh! " (strip-compile-error (.getMessage e))))))

(defroutes app-routes
  (POST "/" [name] (str "Hello " name))
  (POST "/slack-repl"
    [token
      team_id
      team_domain
      channel_id
      channel_name
      user_id
      user_name
      command
      text ;this will contain the code to eval
      response_url]
    (run-command text))
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes settings))
