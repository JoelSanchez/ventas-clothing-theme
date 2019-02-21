(ns ventas.themes.clothing.components.footer
  (:require
   [ventas.components.base :as base]
   [ventas.i18n :refer [i18n]]
   [ventas.routes :as routes]
   [re-frame.core :as rf]))

(defn footer []
  (let [{:keys [phone-number email]} @(rf/subscribe [:db [:theme-configuration]])]
    [:div.footer
     [base/container
      [:div.footer__columns

       [:div.footer__column
        [:h4 (i18n ::links)]
        [:ul
         [:li
          [:a {:href (routes/path-for :frontend.privacy-policy)}
           (i18n ::privacy-policy)]]]]

       [:div.footer__column
        [:h4 (i18n ::contact)]
        [:p "Phone number: " phone-number]
        [:p "Email: " email]]]]]))
