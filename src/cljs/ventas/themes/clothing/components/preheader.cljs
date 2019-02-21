(ns ventas.themes.clothing.components.preheader
  (:require
   [ventas.components.base :as base]
   [ventas.i18n :refer [i18n]]
   [re-frame.core :as rf]))

(defn preheader []
  (let [{:keys [phone-number schedule]} @(rf/subscribe [:db [:theme-configuration]])]
    [:div.preheader
     [base/container
      [:div.preheader__item
       [:strong (i18n ::support-and-orders)]
       [:a phone-number]]
      [:div.preheader__separator "-"]
      [:div.preheader__item
       [:strong (i18n ::schedule)]
       [:span schedule]]]]))
