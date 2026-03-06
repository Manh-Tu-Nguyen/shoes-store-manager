import { createRouter, createWebHashHistory } from "vue-router"

import Login from "../views/Login.vue"
import AdminDashboard from "../views/AdminDashboard.vue"
import Employee from "../views/Employee.vue"
import Customer from "../views/Customer.vue"
import Shift from "../views/Shift.vue"

const routes = [

  {
    path: "/",
    component: Login
  },

  {
    path: "/admin",
    component: AdminDashboard
  },

  {
    path: "/employee",
    component: Employee
  },

  {
    path: "/customer",
    component: Customer
  },

  {
    path: "/shift",
    component: Shift
  }

]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router