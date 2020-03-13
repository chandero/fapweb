<template >
  <el-container>
    <el-header>
      <span>Caja Diario</span>
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
    <el-dialog :visible.sync="dialogVisible" width="30%">
      <span slot="title"><el-icon-info/>Información</span>
      <span>El Libro Caja Diario Fué Generado, puede verificarlo por la página de consulta</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cerrar</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>
<script>
import { mapGetters } from 'vuex'

import { genLibroCajaDiario } from '@/api/informe'

export default {
  data() {
    return {
      form: {
        periodo: null,
        anho: new Date().getFullYear(),
        definitivo: false
      },
      dialogVisible: false
    }
  },
  computed: {
    ...mapGetters([
      'periodos'
    ])
  },
  methods: {
    generar() {
      genLibroCajaDiario(this.form.periodo, this.form.anho, this.form.definitivo).then(response => {
        console.log('resultado: "' + response.data + '"')
        if (response.data === true) {
          this.dialogVisible = true
        } else {
          this.dialogVisible = false
        }
        console.log('data: ' + JSON.stringify(response))
      })
    }
  }
}
</script>
