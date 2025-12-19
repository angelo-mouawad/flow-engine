import { createRouter, createWebHistory } from 'vue-router';
import { useRoleStore } from '../stores/roleStore';
import WelcomeGrid from '../views/Dashboard.vue';
import FlowDefinitionForm from '../components/flows/FlowDefinitionForm.vue';
import FlowDefinitionOverview from '../views/FlowDefinitionOverview.vue';
import FlowDefinitionDetails from '../views/FlowDefinitionDetails.vue';
import FlowInstancesOverview from '../views/FlowInstancesOverview.vue';
import FlowInstanceDetails from '../views/FlowInstanceDetails.vue';
import RequestDetails from '../views/RequestDetails.vue';
import PendingRequestsOverview from '../views/PendingRequestsOverview.vue';

const routes = [
  { path: '/', name: 'Home', component: WelcomeGrid, meta: { title: 'Home | GlacKIT' } },
  { path: '/flow-definitions', name: 'FlowDefinitions', component: FlowDefinitionOverview, meta: { title: 'Flow Definitions | GlacKIT' } },
  { path: '/flow-definitions/:id', name: 'FlowDefinitionDetails', component: FlowDefinitionDetails, meta: { title: 'Flow Definitions | GlacKIT' } },
  { path: '/flow-instances', name: 'FlowInstances', component: FlowInstancesOverview, meta: { title: 'Flow Instances | GlacKIT' } },
  { path: '/flow-instances/:id', name: 'FlowInstanceDetails', component: FlowInstanceDetails, meta: { title: 'Flow Instances | GlacKIT' } },
  { path: '/flow-definitions/new', name: 'FlowDefinitionsNew', component: FlowDefinitionForm, meta: { requiresAdmin: true, title: 'Flow Definitions | GlacKIT' } },
  { path: '/pending-requests', name: 'PendingRequests', component: PendingRequestsOverview, meta: { title: 'Manage Requests | GlacKIT' } },
  { path: '/pending-requests/:id', name: 'RequestDetails', component: RequestDetails, meta: { title: 'Manage Requests | GlacKIT' } },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const roleStore = useRoleStore();
  console.log(from);

  if (to.meta.requiresAdmin && roleStore.role !== 'Manager') {
    alert('You do not have permission to access this page.');
    return next({ path: '/' });

  }

  next();
});

router.afterEach((to) => {
  const title = to.meta?.title as string | undefined;
  if (title) {
    document.title = title;
  }
});

export default router;
