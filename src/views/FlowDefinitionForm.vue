<script setup lang="ts">

</script>

<template>
    <div class="rounded-md border p-5">
        <h1 class="font-extrabold text-4xl pt-5">Create New Flow Definition</h1>
        <RouterLink to="/flow-definitions" class="flex items-center justify-between mb-2">
            &lt; Return To Flow Definitions
        </RouterLink>
        <p class="font-bold ">Basic Information</p>
        <hr>
        <form class="mt-5 flex flex-direction flex-wrap space-y-4">
            <div class="mb-4 grid grid-cols-2 gap-4 w-full">
                <div>
                    <label class="block text-gray-700 text-sm font-bold mb-2" for="flowName">
                        Flow Name
                    </label>
                    <input
                    class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                    id="flowName"
                    type="text"
                    placeholder="Enter flow name"
                    />
                </div>
                <div>
                    <label class="block text-gray-700 text-sm font-bold mb-2" for="flowType">
                        Flow Type
                    </label>
                    <select
                        id="flowType"
                        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        >
                        <option class="text-gray-700 " value="">Select flow type</option>
                        <option class="text-gray-700 " value="type1">Type 1</option>
                        <option class="text-gray-700 " value="type2">Type 2</option>
                        <option class="text-gray-700 " value="type3">Type 3</option>
                    </select>
                </div>
            </div>
            <div class="mb-4 w-full">
                <label class="block text-gray-700 text-sm font-bold mb-2" for="description">
                    Description
                </label>
                <textarea
                    class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                    id="description"
                    rows="4"
                    placeholder="Describe what this flow does"
                    ></textarea>
            </div>
        </form>
        <p class="font-bold ">Basic Information</p>
        <hr class="pb-5">
        <div class="rounded-md border p-5 bg-[#f5f5f5]">
            <p class="font-bold text-gray-700">Step 1</p>
            <label class="block text-sm font-bold mb-2 pt-5" for="stepName">
                Step Name
            </label>
            <input
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="stepName"
            type="text"
            placeholder="e.g., Manager Approval"
            />
            <div class="mb-4 grid grid-cols-2 gap-4 w-full pt-5">
                <div>
                    <label class="block text-gray-700 text-sm font-bold mb-2" for="stepType">
                        Step Type
                    </label>
                    <select
                        id="flowType"
                        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        >
                        <option class="text-gray-700 " value="">Select type</option>
                        <option class="text-gray-700 " value="type1">Type 1</option>
                        <option class="text-gray-700 " value="type2">Type 2</option>
                        <option class="text-gray-700 " value="type3">Type 3</option>
                    </select>
                </div>
                <div>
                    <label class="block text-gray-700 text-sm font-bold mb-2" for="flowType">
                        Assigned Role
                    </label>
                    <select
                        id="flowType"
                        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                        >
                        <option class="text-gray-700 " value="">Select role</option>
                        <option class="text-gray-700 " value="type1">Type 1</option>
                        <option class="text-gray-700 " value="type2">Type 2</option>
                        <option class="text-gray-700 " value="type3">Type 3</option>
                    </select>
                </div>

            </div>
            <div class="mb-4 w-full">
                <label class="block text-gray-700 text-sm font-bold mb-2" for="description">
                    Description
                </label>
                <textarea
                    class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                    id="description"
                    rows="4"
                    placeholder="Enter flow description"
                    ></textarea>
            </div>
        </div>
        <a href="">
            <div class="rounded-md border mt-5 h-25 text-center flex items-center justify-center border-dashed">
                <p class="font-bold">+ Add Step</p>
            </div>
        </a>
        <hr class="mt-5">
        <div class="p-5 flex justify-center gap-[2.6rem]">
            <RouterLink to="/flow-definitions" class="bg-white hover:bg-gray-200 text-gray-800 font-semibold py-2 px-4 border border-gray-400 rounded-md shadow h-13 w-32 text-xl flex items-center justify-center">
                Cancel
            </RouterLink>
            <button class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 border border-black-700 rounded-md h-13 w-40 text-xl">
                Save as Draft
            </button>
            <button class="bg-yellow-500 hover:bg-yellow-700 text-black font-bold py-2 px-4 border border-yellow-700 rounded-md h-13 w-32 text-xl">
                Save
            </button>
        </div>
    </div>
</template>
