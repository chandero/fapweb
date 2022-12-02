<template>
    <div class="userinfo">
        <svg-icon icon-class="user"></svg-icon>
        <span>{{ name }}</span>
    </div>
</template>
<script>
import { mapGetters } from 'vuex'

export default {
  name: 'UserInfo',
  computed: {
    ...mapGetters([
      'name'
    ])
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .userinfo {
    display: table-cell;
    text-transform: uppercase;
    font-size: 14px;
    line-height: 25px;
    margin-left: 10px;
  }
</style>
