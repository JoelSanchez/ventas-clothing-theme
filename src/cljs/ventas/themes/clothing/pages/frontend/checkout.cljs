(ns ventas.themes.clothing.pages.frontend.checkout
  (:require
   [re-frame.core :as rf]
   [ventas.common.utils :as common.utils]
   [ventas.components.base :as base]
   [ventas.components.cart :as cart]
   [ventas.components.form :as form]
   [ventas.components.payment :as payment]
   [ventas.server.api.user :as api.user]
   [ventas.i18n :refer [i18n]]
   [ventas.routes :as routes]
   [ventas.session :as session]
   [ventas.themes.clothing.components.address :as theme.address]
   [ventas.themes.clothing.components.skeleton :as theme.skeleton]
   [ventas.themes.clothing.pages.frontend.profile.addresses :as profile.addresses]
   [ventas.themes.clothing.pages.frontend.checkout.success]
   [ventas.utils.formatting :as utils.formatting]))

(def state-key ::state)

(def new-address-db-path [state-key :new-address])

(rf/reg-event-db
 ::set-shipping-method
 (fn [db [_ v]]
   (assoc-in db [state-key :shipping-method] v)))

(rf/reg-event-fx
 ::set-payment-method
 (fn [{:keys [db]} [_ v]]
   {:db (assoc-in db [state-key :payment-method] v)
    :dispatch [::payment/set-errors nil]}))

(rf/reg-event-db
 ::set-shipping-address
 (fn [db [_ v]]
   (assoc-in db [state-key :shipping-address] v)))

(rf/reg-event-fx
 ::order.next.success
 (fn [{:keys [db]} _]
   {:db (assoc-in db [state-key :loading] false)
    :go-to [:frontend.checkout.success]
    :dispatch [::cart/clear]}))

(rf/reg-event-db
 ::order.next.error
 (fn [db _]
   (assoc-in db [state-key :loading] false)))

(rf/reg-event-fx
 ::order.next
 (fn [{:keys [db]} [_ payment-params]]
   (let [{:keys [shipping-method payment-method]} (get db state-key)
         email (:email (form/get-data db [state-key :contact-information]))
         shipping-address (let [address (get-in db [state-key :shipping-address])]
                            (if-not (= -1 address)
                              address
                              (->> (form/get-data db new-address-db-path)
                                   (common.utils/map-keys #(keyword (name %))))))]
     {:dispatch [::api.user/users.cart.order
                 {:params {:payment-params payment-params
                           :email email
                           :shipping-address shipping-address
                           :shipping-method shipping-method
                           :payment-method payment-method}
                  :success [::order.next.success]
                  :error [::order.next.error]}]})))

(rf/reg-event-fx
 ::order
 (fn [{:keys [db]} _]
   (let [{:keys [payment-method]} (get db state-key)
         {:keys [submit-fx]} (get (payment/get-methods) payment-method)]
     {:dispatch (if submit-fx
                  (conj submit-fx {:success [::order.next]
                                   :error [::order.next.error]})
                  [::order.next])
      :db (assoc-in db [state-key :loading] true)})))

(defn contact-information []
  [:div.checkout-page__contact-information
   [base/header {:as "h3"
                 :attached "top"
                 :class "checkout-page__contact-info"}
    [:span (i18n ::contact-information)]
    [:div.checkout-page__login
     (i18n ::already-registered)
     " "
     [:a {:href (routes/path-for :frontend.login)}
      (i18n ::login)]]]
   (let [db-path [state-key :contact-information]]
     [base/segment {:attached true}
      [form/form db-path
       [base/form
        [form/field {:db-path db-path
                     :label (i18n ::email)
                     :key :email}]]]])])

(defn address []
  [:div.checkout-page__address
   [base/header {:as "h3"
                 :attached "top"}
    (i18n ::shipping-address)]
   [base/segment {:attached true}
    (let [shipping-address @(rf/subscribe [:db [state-key :shipping-address]])]
      [:div.checkout-page__address-inner
       [base/form
        (doall
         (for [{:keys [id] :as address} @(rf/subscribe [:db [state-key :addresses]])]
           [base/segment
            [base/form-radio {:value id
                              :checked (= shipping-address id)
                              :on-change #(rf/dispatch [::set-shipping-address id])}]
            [profile.addresses/address-content-view address]]))
        [base/segment
         [base/form-radio {:value -1
                           :checked (= -1 shipping-address)
                           :on-change #(rf/dispatch [::set-shipping-address -1])}]
         [:span (i18n ::new-address)]]]
       (when (= shipping-address -1)
         [theme.address/address new-address-db-path])])]])

(defn shipping-methods []
  [:div.checkout-page__shipping-methods
   [base/header {:as "h3"
                 :attached "top"}
    (i18n ::shipping-method)]
   [base/segment {:attached true}
    (let [selected @(rf/subscribe [:db [state-key :shipping-method]])
          methods @(rf/subscribe [:db [state-key :shipping-methods]])]
      (doall
       (for [{:keys [id name price]} methods]
         [:div.shipping-method
          [base/segment
           [base/form-radio
            {:value id
             :checked (= id selected)
             :on-change #(rf/dispatch [::set-shipping-method (aget %2 "value")])}]
           [:span.shipping-method__name name]
           [:span.shipping-method__price (utils.formatting/amount->str price)]]])))]])

(defn payment-methods []
  [:div.checkout-page__payment-methods
   [base/header {:as "h3"
                 :attached "top"}
    (i18n ::payment-method)]
   [base/segment {:attached true}
    (let [selected @(rf/subscribe [:db [state-key :payment-method]])
          methods @(rf/subscribe [:db [state-key :payment-methods]])
          payment-methods (payment/get-methods)]
      [base/accordion {:fluid true
                       :styled true}
       [:div.payment-methods
        (map-indexed
         (fn [idx [id {:keys [name]}]]
           [:div.payment-method
            [base/accordion-title {:active (= selected id)
                                   :index idx
                                   :on-click #(rf/dispatch [::set-payment-method id])}
             [:span name]]
            [base/accordion-content {:active (= selected id)}
             [:div
              (when-let [component (get-in payment-methods [id :component])]
                [component])]]])
         methods)]])]])

(rf/reg-sub
 ::valid?
 (fn [db]
   (let [address (get-in db [state-key :shipping-address])]
     (and (or (not= -1 address)
              @(rf/subscribe [::form/valid? new-address-db-path]))
          (empty? @(rf/subscribe [::payment/errors]))))))

(defn page []
  [theme.skeleton/skeleton
   [base/container
    [:div.checkout-page
     [:h2 (i18n ::checkout)]
     [:div.checkout-page__content
      [:div
       (when-not @(rf/subscribe [::session/identity.valid?])
         [:div
          [contact-information]
          [base/divider {:hidden true}]])
       [address]
       [base/divider {:hidden true}]
       [shipping-methods]
       [base/divider {:hidden true}]
       [payment-methods]
       [base/divider {:hidden true}]
       [base/button {:type "button"
                     :size "large"
                     :fluid true
                     :disabled (not @(rf/subscribe [::valid?]))
                     :loading @(rf/subscribe [:db [state-key :loading]])
                     :on-click #(rf/dispatch [::order])}
        (i18n ::order)]]]]]])

(rf/reg-event-fx
 ::init.addresses.next
 (fn [_ [_ data]]
   {:dispatch-n [[:db [state-key :addresses] data]
                 [::set-shipping-address (-> data first :id)]]}))

(rf/reg-event-fx
 ::init
 (fn [_ _]
   {:dispatch-n [[::cart/get]
                 [::api.user/users.addresses
                  {:success ::init.addresses.next}]
                 [::api.user/users.cart.shipping-methods
                  {:success ::init.shipping-methods.next}]
                 [::api.user/users.cart.payment-methods
                  {:success ::init.payment-methods.next}]
                 [::theme.address/init new-address-db-path]]}))

(rf/reg-event-fx
 ::init.shipping-methods.next
 (fn [_ [_ methods]]
   {:dispatch-n [[:db [state-key :shipping-methods] methods]
                 [::set-shipping-method (-> methods first :id)]]}))

(rf/reg-event-fx
 ::init.payment-methods.next
 (fn [_ [_ methods]]
   {:dispatch-n (->> methods
                     (map (fn [[id _]]
                            (when-let [init-fx (-> (payment/get-methods) id :init-fx)]
                              init-fx)))
                     (into [[:db [state-key :payment-methods] methods]
                            [::set-payment-method (some-> methods first key)]]))}))

(routes/define-route!
  :frontend.checkout
  {:name ::page
   :url ["checkout"]
   :component page
   :init-fx [::init]})
