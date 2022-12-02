<template>
  <el-container>
    <el-header>
      <h1>Informe Personas Creditos Saldados - Asesor</h1>
    </el-header>
    <el-main>
      <el-form ref="form" label-position="top">
        <el-row>
          <el-col :span="12">
              <el-form-item label="Asesor">
                <el-select v-model="ases_id" placeholder="Seleccione el Asesor" style="width: 90%">
                  <el-option v-for="a in asesores" :key="a.id" :label="a.descripcion" :value="a.id" />
                </el-select>
              </el-form-item>
          </el-col>
          <el-col>
            <el-form-item>
              <el-button type="primary" @click="generarXlsx">Generar</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-main>
  </el-container>
</template>
<script>
import { obtenerListaAsesores } from '@/api/tipos'
import { informeColocacionSaldadoAsesor } from '@/api/informe'
export default {
    data() {
      return {
        ases_id: null,
        asesores: []
      }
    },
    methods: {
      getAsesores() {
        obtenerListaAsesores().then(response => {
          this.asesores = response.data
        })
      },
      generarXlsx() {
        informeColocacionSaldadoAsesor(this.ases_id).then(response => {
          const filename = decodeURI(response.headers['content-disposition'].split('filename=')[1])
          const a = document.createElement('a')
          document.body.appendChild(a)
          a.href = window.URL.createObjectURL(response.data)
          a.download = filename
          a.target = '_self'
          a.click()
          a.remove()
        })
      }
    },
    beforeMount() {
      this.getAsesores()
    }
}
</script>