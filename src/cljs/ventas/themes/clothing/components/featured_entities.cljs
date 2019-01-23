(ns ventas.themes.clothing.components.featured-entities
  (:require
   [re-frame.core :as rf]
   [ventas.themes.clothing.api :as api]
   [ventas.components.category-list :refer [category-list]]
   [ventas.components.product-list :refer [product-list]]))

(def state-key ::state)

(defn main [key]
  (let [{:keys [entities type]} @(rf/subscribe [:db [state-key key]])]
    (case type
      :product [product-list entities]
      :category [category-list entities]
      nil)))

(rf/reg-event-fx
 ::init
 (fn [_ [_ key entity-type]]
   {:pre [(#{:category :product} entity-type) key]}
   {:dispatch [::api/featured-entities.list
               {:params {:entity-type entity-type}
                :success [:db [state-key key]]}]}))