import { createRouter, createWebHistory, type RouteRecordRaw } from "vue-router";
import { useUserStore } from "../stores/userStore";
import Dashboard from "../views/Dashboard.vue";
import FlowDefinitionForm from "../components/flows/FlowDefinitionForm.vue";
import FlowDefinitionOverview from "../views/FlowDefinitionOverview.vue";
import FlowDefinitionDetails from "../views/FlowDefinitionDetails.vue";
import FlowInstancesOverview from "../views/FlowInstancesOverview.vue";
import FlowInstanceDetails from "../views/FlowInstanceDetails.vue";
import RequestDetails from "../views/RequestDetails.vue";
import PendingRequestsOverview from "../views/PendingRequestsOverview.vue";

declare module "vue-router" {
  interface RouteMeta {
    title?: string;
    requiresManager?: boolean;
  }
}

const routes: RouteRecordRaw[] = [
  { path: "/", name: "Home", component: Dashboard, meta: { title: "Home | GlacKIT" } },
  {
    path: "/flow-definitions",
    name: "FlowDefinitions",
    component: FlowDefinitionOverview,
    meta: { title: "Flow Definitions | GlacKIT", requiresManager: true },
  },
  {
    path: "/flow-definitions/new",
    name: "FlowDefinitionsNew",
    component: FlowDefinitionForm,
    meta: { title: "New Flow Definition | GlacKIT", requiresManager: true },
  },
  {
    path: "/flow-definitions/:id",
    name: "FlowDefinitionDetails",
    component: FlowDefinitionDetails,
    meta: { title: "Flow Definitions | GlacKIT", requiresManager: true },
  },
  { path: "/flow-instances", name: "FlowInstances", component: FlowInstancesOverview, meta: { title: "Flow Instances | GlacKIT" } },
  { path: "/flow-instances/:id", name: "FlowInstanceDetails", component: FlowInstanceDetails, meta: { title: "Flow Instances | GlacKIT" } },
  {
    path: "/pending-requests",
    name: "PendingRequests",
    component: PendingRequestsOverview,
    meta: { title: "Manage Requests | GlacKIT", requiresManager: true },
  },
  {
    path: "/pending-requests/:id",
    name: "RequestDetails",
    component: RequestDetails,
    meta: { title: "Manage Requests | GlacKIT", requiresManager: true },
  },
  { path: "/:pathMatch(.*)*", redirect: "/" },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to) => {
  if (to.meta.requiresManager && !useUserStore().isManager) {
    return { name: "Home" };
  }
  return true;
});

router.afterEach((to) => {
  document.title = to.meta.title ?? "GlacKIT";
});

export default router;
