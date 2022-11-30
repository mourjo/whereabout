(ns whereabout.server
  (:require
   [cheshire.core :as cc]
   [clojure.tools.logging :as ctl]
   [com.stuartsierra.component :as component]
   [compojure.core :refer [ANY defroutes GET]]
   [compojure.route :refer [not-found]]
   [ring.adapter.jetty :as jetty]
   [ring.middleware.defaults :as default-middleware]
   [ring.util.response :as rur]
   [whereabout.model :as wm]))

(defn location-handler
  "Given an HTTP request, calls the model if the request is well formed"
  [{:keys [query-params system]}]
  (let [ip (query-params "ip")
        location (when ip (wm/find-location system ip))]
    (if (seq location)
      (-> {:result (wm/find-location system ip)}
          cc/generate-string
          rur/response
          (rur/header "Content-Type" "application/json"))
      (-> {:error "Could not find a location"}
          cc/generate-string
          rur/not-found
          (rur/header "Content-Type" "application/json")))))


(defroutes routes
  ;; Routes available on this server
  (GET "/location" params (location-handler params))
  (ANY "*" _ (not-found "Incorrect route!")))


(defn wrap-system
  "A middleware for dependency injection for the other components of the system the server
  relies on"
  [handler system]
  (fn [request]
    (handler (assoc request :system system))))


(defn app
  "Builds a runnable app after applying middleware to the server handler"
  [system]
  (-> routes
      (default-middleware/wrap-defaults default-middleware/site-defaults)
      (wrap-system system)))


(defrecord Server [port db]
  component/Lifecycle
  (start [component]
    (if (or (:server component) (not port))
      component
      (let [server (jetty/run-jetty (app {:db db})
                                    {:port port :join? false})]
        (ctl/info "Started HTTP server on " port)
        (assoc component :server server))))
  (stop [component]
    (.stop ^org.eclipse.jetty.server.Server (:server component))
    (assoc component :server nil)))
