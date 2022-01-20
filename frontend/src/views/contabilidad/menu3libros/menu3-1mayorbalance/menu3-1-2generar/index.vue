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
          <el-button type="primary" @click="generar">Generar</el-button>
        </el-form-item>
      </el-form>
    </el-main>
    <el-dialog :visible.sync="dialogVisible" width="30%">
      <span slot="title"><el-icon-info/>Información</span>
      <span>El Libro Mayor Fué Generado, puede verificarlo por la página de consulta</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cerrar</el-button>
      </span>
    </el-dialog>
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
      const loading = this.$loading({
        lock: true,
        text: 'Generando Libro',
      });      
      genLibroMayor(this.form.periodo, this.form.anho).then(response => {
        loading.close()
        if (response.data === true) {
          this.dialogVisible = true
        } else {
          this.dialogVisible = false
        }
      }).catch(err => {
        loading.close()
        this.$message({
          type: 'error',
          message: 'Error al generar el Libro Mayor :(' + err + ')'
        })
      })
    }
  }
}
</script>
