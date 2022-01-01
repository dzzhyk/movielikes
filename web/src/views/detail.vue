<template>
  <div>detail {{ movieId }}</div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'

const route = useRoute();
const router = useRouter();
const { params, query } = route
const { movieId } = params

</script>