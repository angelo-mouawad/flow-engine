import { createRouter, createWebHistory } from 'vue-router';
import WelcomeGrid from '../views/WelcomeGrid.vue';
import FlowDefinitionForm from '../views/FlowDefinitionForm.vue';
import FlowDetails from '../components/FlowDetails.vue';

const routes = [
  { path: '/', name: 'Home', component: WelcomeGrid },
  { path: '/flow-definitions', name: 'FlowDefinitions', component: FlowDetails, props: { isDefinition: true }},
  { path: '/flow-instances', name: 'FlowInstances', component: FlowDetails, props: { isDefinition: false }},
  { path: '/flow-definitions/new', name: 'FlowDefinitionsNew', component: FlowDefinitionForm  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
