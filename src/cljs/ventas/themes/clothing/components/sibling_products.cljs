(ns ventas.themes.clothing.components.sibling-products
  (:require
   [re-frame.core :as rf]
   [ventas.themes.clothing.api :as api]
   [ventas.components.product-list :as components.product-list]))

(def state-key ::state)

(rf/reg-event-fx
 ::list
 (fn [_ [_ product-id]]
   {:dispatch [::api/sibling-products.list
               {:params {:id product-id}
                :success [:db [state-key product-id]]}]}))

(rf/reg-sub
 ::list
 (fn [db [_ product-id]]
   (get-in db [state-key product-id])))

(defn sibling-products [product-id]
  (let [products @(rf/subscribe [::list product-id])]
    [components.product-list/product-list products]))
