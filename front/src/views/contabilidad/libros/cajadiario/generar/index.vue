<template >
  <el-container>
    <el-header>
      <span>Mayor y Balance</span>
    </el-header>
    <el-main>
      <el-form v-model="form" label-width="120px">
        <el-form-item :label="$t('general.periodo')" >
          <el-select v-model="form.periodo">
            <el-option
              v-for="p in periodos"
              :key="p.id"
              :label="$t(p.descripcion)"
              :value="p.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('general.anho')">
          <el-input v-model="form.anho" type="number" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.definitivo">{{ $t('general.definitivo') }}</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="generar">Generar</el-button>
        </el-form-item>
      </el-form>
    </el-main>
  </el-container>
</template>
<script>
import { mapGetters } from 'vuex'

import { genLibroMayor } from '@/api/informe'

export default {
  data() {
    return {
      form: {
        periodo: null,
        anho: new Date().getFullYear(),
        definitivo: false
      }
    }
  },
  computed: {
    ...mapGetters([
      'periodos'
    ])
  },
  methods: {
    generar() {
      genLibroMayor(this.form.periodo, this.form.anho, this.form.definitivo).then(response => {
        console.log('data: ' + JSON.stringify(response))
      })
    }
  }
}
</script>
