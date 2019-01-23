(ns ventas.themes.clothing.api
  (:require
   [re-frame.core :as rf]))

(rf/reg-event-fx
 ::featured-entities.list
 (fn [_ [_ options]]
   {:ws-request (merge {:name ::featured-entities.list}
                       options)}))