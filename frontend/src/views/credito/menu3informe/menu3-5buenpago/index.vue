<template>
  <el-container>
    <el-header>
      <span>Informe Clientes con Buen Comportamiento de Pago</span>
    </el-header>
    <el-main>
      <el-row>
        <el-col>
          <el-form ref="form" :model="form" label-position="top">
            <el-form-item>
              <el-button type="submit" @click="generar" >Generar</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-table :data="tableData" max-height="600">
            <el-table-column label="Documento" prop="documento" />
            <el-table-column label="Primer Apellido" prop="primer_apellido" />
            <el-table-column label="Segundo Apellido" prop="segundo_apellido" />
            <el-table-column label="Nombre Apellido" prop="nombre" />
            <el-table-column label="Teléfono" prop="telefono" />
            <el-table-column label="Máx Mora" prop="dias" />
          </el-table>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <img
            :title="$t('xls')"
            :src="require('@/assets/xls.png')"
            style="width:32px; height: 36px; cursor: pointer;"
            @click="exportarXls()"
          >
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { consultarClienteBuenPago, generarClienteBuenPago, exportarClienteBuenPago } from '@/api/informe'
import { parseTime } from '@/utils'

export default {
  data() {
    return {
      tableData: null,
      form: null,
      fullscreenLoading: false,
      fecha_hoy: new Date()
    }
  },
  mounted() {
    this.consultar()
  },
  methods: {
    consultar() {
      consultarClienteBuenPago().then(response => {
        this.tableData = response.data
      })
    },
    generar() {
      const loading = this.$loading({
        lock: true,
        text: 'Generando, Este proceso puede tardar algunos minutos, sea paciente...',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      })
      generarClienteBuenPago()
      loading.close()
    },
    exportarXls() {
      exportarClienteBuenPago()
    },
    formatJson(filterVal, jsonData) {
      return jsonData.map(v => filterVal.map(j => {
        var result
        if (j === 'fecha') {
          result = parseTime(v[j])
        } else if (v[j] === null || v[j] === undefined) {
          result = ''
        } else {
          result = v[j]
        }
        console.log('return result: ' + result)
        return result
      }))
    }
  }
}
</script>
