(ns ventas.themes.clothing.pages.frontend.profile.skeleton
  (:require
   [re-frame.core :as rf]
   [ventas.components.base :as base]
   [ventas.components.sidebar :as sidebar]
   [ventas.events :as events]
   [ventas.i18n :refer [i18n]]
   [ventas.routes :as routes]
   [ventas.components.notificator :as notificator]
   [ventas.themes.clothing.components.skeleton :as skeleton]
   [ventas.session :as session]))

(defn sidebar []
  [sidebar/sidebar

   [sidebar/sidebar-section {:name (i18n ::my-profile)}

    [sidebar/link {:href (routes/path-for :frontend.profile.orders)}
     (i18n ::my-orders)]

    [sidebar/link {:href (routes/path-for :frontend.profile.addresses)}
     (i18n ::my-addresses)]

    [sidebar/link {:href (routes/path-for :frontend.profile.account)}
     (i18n ::my-account)]

    [sidebar/link {:on-click #(rf/dispatch [::events/users.logout])}
     (i18n ::logout)]]])

(defn skeleton [content]
  [skeleton/skeleton
   [base/container {:class "profile-skeleton"}
    [sidebar]
    [:div.profile-skeleton__content
     content]]])

(rf/reg-event-fx
 ::require-identity
 (fn [{:keys [db]} _]
   (let [{:keys [status] :as identity} (session/get-identity db)]
     (when-not (session/identity-valid? identity)
       (merge {:go-to [:frontend.login]}
              (when (= :user.status/unregistered status)
                {:dispatch [::notificator/add {:message (i18n ::session/unregistered-error)
                                               :theme "error"}]}))))))
