import { createWebHistory, createRouter } from "vue-router";
import Home from "@/views/Home.vue";
import Queries from "@/views/Queries.vue";
import Settings from "@/views/Settings.vue";
import Client from "@/views/Client.vue";
import ClientForm from "@/views/forms/ClientForm.vue";
import TaskForm from "@/views/forms/TaskForm.vue";
import LibraryQueryForm from "@/views/forms/LibraryQueryForm.vue";
import SessionExpired from "@/views/SessionExpired.vue";

import axios from 'axios'


const routes = [
  {
    path: "/", // default page
    name: "home",
    component: Home,
  },
  {
    path: "/client/edit",
    name: "clientNew",
    component: ClientForm,
  },
  {
    path: "/client/edit/:id", // default page
    name: "clientEdit",
    component: ClientForm,
  },
  {
    path: "/client/:id", // client overview page
    name: "client",
    component: Client,
  },
  {
    path: "/task/:id",
    name: "taskEdit",
    component: TaskForm,
  },
  {
    path: "/task/new/:clientid/",
    name: "taskNew",
    component: TaskForm,
  },
  {
    path: "/queries",
    name: "queries",
    component: Queries,
  },
  {
    path: "/query/:id",
    name: "query",
    component: LibraryQueryForm,
  },
  {
    path: "/settings",
    name: "settings",
    component: Settings,
  },
  {
    path: "/sessionexpired",
    name: "sessionexpired",
    component: SessionExpired,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});


// Before each router request check whether the session is still active
router.beforeEach((to,from,next) => {

  // console.log("TO " + to.fullPath)
  // console.log("FROM " + from.fullPath)

  if (to.fullPath != "/login") {

    axios.get('/api/session')
    .then(resp => {
      console.log("Are you still logged in? ")
      if(resp.data=='OK') next()
      else window.location.href = "/login"
    }) 
    .catch(() => {
      console.log("Session expired!")
      window.location.href = "/login"
    }) 
  }  

})


export default router;