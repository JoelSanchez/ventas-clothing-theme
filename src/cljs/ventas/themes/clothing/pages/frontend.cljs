(ns ventas.themes.clothing.pages.frontend
  (:require
   [re-frame.core :as rf]
   [ventas.components.base :as base]
   [ventas.components.category-list :as category-list]
   [ventas.i18n :refer [i18n]]
   [ventas.routes :as routes]
   [ventas.themes.clothing.components.heading :as theme.heading]
   [ventas.themes.clothing.components.skeleton :as theme.skeleton]
   [ventas.themes.clothing.components.featured-entities :as featured-entities]
   [ventas.themes.clothing.pages.frontend.cart]
   [ventas.themes.clothing.pages.frontend.category]
   [ventas.themes.clothing.pages.frontend.checkout]
   [ventas.themes.clothing.pages.frontend.favorites]
   [ventas.themes.clothing.pages.frontend.login]
   [ventas.themes.clothing.pages.frontend.privacy-policy]
   [ventas.themes.clothing.pages.frontend.product]
   [ventas.themes.clothing.pages.frontend.profile]))

(def slider-kw :sample-slider)
(def category-list-key ::category-list)
(def product-list-key ::product-list)

(defn page []
  [theme.skeleton/skeleton
   [:div
    [base/container
     [category-list/category-list]
     [theme.heading/heading (i18n ::suggestions-of-the-week)]
     [featured-entities/main product-list-key]
     [theme.heading/heading (i18n ::recently-added)]
     [featured-entities/main category-list-key]]]])

(rf/reg-event-fx
 ::init
 (fn [_ _]
   {:dispatch-n [[::featured-entities/init category-list-key :category]
                 [::featured-entities/init product-list-key :product]]}))

(routes/define-route!
  :frontend
  {:name ::page
   :url ""
   :component page
   :init-fx [::init]})
