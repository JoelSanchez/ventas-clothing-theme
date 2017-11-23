(ns ventas.pages.admin.configuration
  (:require
   [reagent.core :as reagent :refer [atom]]
   [re-frame.core :as rf]
   [re-frame-datatable.core :as dt]
   [ventas.page :refer [pages]]
   [ventas.pages.admin.skeleton :as admin.skeleton]
   [ventas.routes :as routes]
   [ventas.components.base :as base]
   [ventas.components.datatable :as datatable]
   [ventas.pages.admin.configuration.image-sizes]
   [ventas.i18n :refer [i18n]]))

(def taxes-key ::taxes)

(defn taxes-datatable [action-column]
  (rf/dispatch [:ventas/taxes.list [taxes-key]])
  (fn [action-column]
    (let [id (keyword (gensym "taxes"))]
      [:div
       [dt/datatable id [:ventas/db [taxes-key]]
        [{::dt/column-key [:id]
          ::dt/column-label "#"
          ::dt/sorting {::dt/enabled? true}}

         {::dt/column-key [:name]
          ::dt/column-label (i18n ::name)}

         {::dt/column-key [:quantity]
          ::dt/column-label (i18n ::quantity)
          ::dt/sorting {::dt/enabled? true}}

         {::dt/column-key [:actions]
          ::dt/column-label (i18n ::actions)
          ::dt/render-fn action-column}]

        {::dt/pagination {::dt/enabled? true
                          ::dt/per-page 3}
         ::dt/table-classes ["ui" "table" "celled"]
         ::dt/empty-tbody-component (fn [] [:p (i18n ::no-taxes)])}]
       [:div.admin-taxes__pagination
        [datatable/pagination id [:ventas/db [taxes-key]]]]])))

(defn page []
  [admin.skeleton/skeleton
   "Nothing here for the moment!"])

(routes/define-route!
 :admin.configuration
 {:name (i18n ::page)
  :url "configuration"
  :component page})