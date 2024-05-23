import { 
  //createWebHistory, 
  createWebHashHistory,
  createRouter 
} from "vue-router";
import Home from "@/views/Home.vue";
import Client from "@/views/Client.vue";
import ClientForm from "@/views/forms/ClientForm.vue";
import TaskForm from "@/views/forms/TaskForm.vue";
import Library from "@/views/Library.vue";
import LibraryQuick from "@/views/LibraryQuick.vue";
import LibraryQueryForm from "@/views/forms/LibraryQueryForm.vue";
import LibraryScriptForm from "@/views/forms/LibraryScriptForm.vue";
import Settings from "@/views/Settings.vue";
import SessionExpired from "@/views/SessionExpired.vue";
import RunLogAll from "@/views/RunLogAll.vue";
import Admin from "@/views/Admin.vue";

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
    // popup while editing a script in Task view
    path: "/libraryquick",
    name: "libraryQuick",
    component: LibraryQuick,
  },
  {
    path: "/library",
    name: "library",
    component: Library,
  },
  {
    path: "/query",
    name: "queryNew",
    component: LibraryQueryForm,
  },
  {
    path: "/query/:id",
    name: "query",
    component: LibraryQueryForm,
  },
  {
    path: "/script",
    name: "scriptNew",
    component: LibraryScriptForm,
  },
  {
    path: "/script/:id",
    name: "script",
    component: LibraryScriptForm,
  },
  {
    path: "/settings",
    name: "settings",
    component: Settings,
  },
  {
    path: "/alllogs",
    name: "alllogs",
    component: RunLogAll,
  },
  {
    path: "/sessionexpired",
    name: "sessionexpired",
    component: SessionExpired,
  },
  {
    path: "/admin",
    name: "admin",
    component: Admin,
  },

];

const router = createRouter({
  history: createWebHashHistory(),
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