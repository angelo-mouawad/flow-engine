const loading = ref(false);
const requests = ref<RequestSubmission[]>([]);
const error = ref<string | null>(null);

const sortKey = ref<'Request Type' | 'Submitted By' | 'Start Date' | 'End Date' | 'Reason'>('Start Date');
const sortOrder = ref<'asc' | 'desc'>('asc');
const selectedRequests = computed<RequestSubmission[]>(() =>
    requests.value.filter(r => !!(r as any)._selected)
);
const unselectedRequests = computed<RequestSubmission[]>(() =>
    requests.value.filter(r => !(r as any)._selected)
);

const approveSelectedRequests = async () => {
    if (selectedRequests.value.length === 0) return;
    loading.value = true;
    error.value = null;
    try {
        await Promise.all(
            selectedRequests.value.map(r => RequestService.approveRequest(r.id))
        );

        const approvedIds = new Set(selectedRequests.value.map(r => r.id));
        requests.value = requests.value.filter(r => !approvedIds.has(r.id));
    } catch (e) {
        error.value = e instanceof Error ? e.message : 'An error occurred while approving requests';
        console.error('Failed to approve requests:', e);
    } finally {
        loading.value = false;
    }
};

const declineUnselectedRequests = async () => {
    if (unselectedRequests.value.length === 0) return;
    loading.value = true;
    error.value = null;
    try {
        await Promise.all(
            unselectedRequests.value.map(r => RequestService.declineRequest(r.id))
        );

        const approvedIds = new Set(unselectedRequests.value.map(r => r.id));
        requests.value = requests.value.filter(r => !approvedIds.has(r.id));
    } catch (e) {
        error.value = e instanceof Error ? e.message : 'An error occurred while approving requests';
        console.error('Failed to approve requests:', e);
    } finally {
        loading.value = false;
    }
};

const sortFlowInstances = (key: 'Request Type' | 'Submitted By' | 'Start Date' | 'End Date' | 'Reason') => {
    if (sortKey.value === key) {
        sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
    } else {
        sortKey.value = key;
        sortOrder.value = 'asc';
    }
    requests.value = [...requests.value].sort((a, b) => {
        let result = 0;
        if (key === 'Request Type') {
            if (a.requestType.toLowerCase() < b.requestType.toLowerCase()) result = -1;
            if (a.requestType.toLowerCase() > b.requestType.toLowerCase()) result = 1;
        } else if (key === 'Submitted By') {
            if (a.data.allFields.submittedBy.toLowerCase() < b.data.allFields.submittedBy.toLowerCase()) result = -1;
            if (a.data.allFields.submittedBy.toLowerCase() > b.data.allFields.submittedBy.toLowerCase()) result = 1;
        } else if (key === 'Start Date') {
            result = new Date(a.data.allFields.startDate).getTime() - new Date(b.data.allFields.startDate).getTime();
        } else if (key === 'End Date') {
            result = new Date(a.data.allFields.endDate).getTime() - new Date(b.data.allFields.endDate).getTime();
        } else if (key === 'Reason') {
            if (a.data.allFields.reason.toLowerCase() < b.data.allFields.reason.toLowerCase()) result = -1;
            if (a.data.allFields.reason.toLowerCase() > b.data.allFields.reason.toLowerCase()) result = 1;
        }
        return sortOrder.value === 'asc' ? result : -result;
    });
};

const fetchRequests = async () => {
    loading.value = true;
    error.value = null;
    try {
        const data = await RequestService.getRequests();
        requests.value = data;
        sortFlowInstances(sortKey.value);
    } catch (e) {
        error.value = e instanceof Error ? e.message : 'An error occurred while fetching flow instances';
        console.error('Failed to fetch flow instances:', e);
    } finally {
        loading.value = false;
    }
}

onMounted(() => {
    fetchRequests();
});
</script>

<template>
    <table class="w-full border-collapse text-sm">
        <thead>
            <tr class="text-left text-white bg-[#111]">
                <th class="px-4 py-2 font-medium select-none cursor-pointer" @click="sortFlowInstances('Request Type')">
                    Request Type
                    <span class="ml-2 inline-block" style="width: 1.5em; text-align: left;">
                        <span v-if="sortKey === 'Request Type'">
                            <span v-if="sortOrder === 'asc'">▲</span>
                            <span v-else>▼</span>
                        </span>
                        <span v-else class="text-gray-400">▲▼</span>
                    </span>
                </th>
                <th class="px-4 py-2 font-medium select-none cursor-pointer" @click="sortFlowInstances('Submitted By')">
                    Submitted By
                    <span class="ml-2 inline-block" style="width: 1.5em; text-align: left;">
                        <span v-if="sortKey === 'Submitted By'">
                            <span v-if="sortOrder === 'asc'">▲</span>
                            <span v-else>▼</span>
                        </span>
                        <span v-else class="text-gray-400">▲▼</span>
                    </span>
                </th>
                <th class="px-4 py-2 font-medium select-none cursor-pointer"
                    @click="sortFlowInstances('Start Date')">
                    Start Date
                    <span class="ml-2 inline-block" style="width: 1.5em; text-align: left;">
                        <span v-if="sortKey === 'Start Date'">
                            <span v-if="sortOrder === 'asc'">▲</span>
                            <span v-else>▼</span>
                        </span>
                        <span v-else class="text-gray-400">▲▼</span>
                    </span>
                </th>
                <th class="px-4 py-2 font-medium select-none cursor-pointer" @click="sortFlowInstances('End Date')">
                    End Date
                    <span class="ml-2 inline-block" style="width: 1.5em; text-align: left;">
                        <span v-if="sortKey === 'End Date'">
                            <span v-if="sortOrder === 'asc'">▲</span>
                            <span v-else>▼</span>
                        </span>
                        <span v-else class="text-gray-400">▲▼</span>
                    </span>
                </th><th class="px-4 py-2 font-medium select-none cursor-pointer" @click="sortFlowInstances('End Date')">
                    Reason
                    <span class="ml-2 inline-block" style="width: 1.5em; text-align: left;">
                        <span v-if="sortKey === 'Reason'">
                            <span v-if="sortOrder === 'asc'">▲</span>
                            <span v-else>▼</span>
                        </span>
                        <span v-else class="text-gray-400">▲▼</span>
                    </span>
                </th>
                <th>Approve Request</th>
            </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
            <div v-if="loading" class="flex items-center justify-center py-8">
                <div class="text-gray-600">Loading requests...</div>
            </div>

            <div v-else-if="error" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
                <p class="font-medium">Error</p>
                <p class="text-sm">{{ error }}</p>

            <tr class="cursor-pointer hover:bg-gray-100" v-for="requ in requests" :key="requ.id">
                <td class="px-4 py-2 font-medium">{{ requ.requestType }}</td>
                <td class="px-4 py-2 text-gray-600">{{ requ.data.allFields.submittedBy }}</td>
                <td class="px-4 py-2 text-gray-600">{{ requ.data.allFields.startDate }}</td>
                <td class="px-4 py-2 text-gray-600">{{ requ.data.allFields.endDate }}</td>
                <td class="px-4 py-2 text-gray-600">{{ requ.data.allFields.reason }}</td>
                <td class="px-4 py-2 text-gray-600"></td>
            </tr>
        </tbody>
    </table>
    <div class="mt-4 flex flex-col mx-6 space-x-3">
        <button
            class="px-4 w-[100px] mb-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
            :disabled="loading"
            @click="(async () => { await approveSelectedRequests(); await declineUnselectedRequests(); })()"
        >
            {{ loading ? 'Submitting...' : 'Submit' }}
        </button>

        <div class="text-sm text-gray-600" v-if="requests.length">
            <span>{{ selectedRequests.length }} checked</span>
            <span class="mx-2">·</span>
            <span>{{ unselectedRequests.length }} unchecked</span>
        </div>
    </div>
</template>
