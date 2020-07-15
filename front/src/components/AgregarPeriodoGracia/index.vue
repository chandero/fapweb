<template>
  <el-container>
    <el-main>
      <el-row :gutter="4">
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
          <buscar-colocacion-persona :tipo="id_identificacion" :documento="id_persona" :colocacion="id_colocacion" @setPersonEvent="setDatosPersonaDesdeElEvento" />
        </el-col>
      </el-row>
      <el-row>
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
          <span>Colocaciones</span>
        </el-col>
      </el-row>
      <el-row :gutter="4">
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
          <el-table v-loading="loading" :data="dataColocacion" :row-class-name="tableRowClassName" highlight-current-row style="width: 100%; font-size: 12px;" max-height="350">
            <el-table-column type="selection" width="55" />
            <el-table-column sortable label="D/F" prop="tipo" width="80">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.tipo }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Colocación" prop="id_colocacion" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.id_colocacion }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Saldo" prop="saldo" align="right" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">${{ scope.row.saldo | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Capital" prop="fecha_capital" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_capital | moment("YYYY-MM-DD") }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Interés" prop="fecha_interes" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_interes | moment("YYYY-MM-DD") }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Estado" prop="estado" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.estado }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Id" prop="tipo_id" width="60">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.id_identificacion }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Identificación" prop="documento" width="130">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.id_persona }}</span>
              </template>
            </el-table-column>
            <el-table-column
              fixed="right"
              label="Ver"
              width="140">
              <template slot-scope="scope">
                <el-button type="primary" mini circle icon="el-icon-plus" @click="activarAgregar(scope.row.id_colocacion)"/>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-main>
    <el-dialog :visible.sync="showAddDlg" :title="'Agregar Periodo de Gracia a Colocacion: ' + addform.id_colocacion" :destroy-on-close="true" append-to-body>
      <el-form>
        <el-row :gutter="4">
          <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
            <el-form-item label="Fecha Registro">
              <el-date-picker v-model="addform.fecha" type="datetime" format="yyyy/MM/dd HH:mm:ss"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
            <el-form-item label="Días">
              <el-input v-model="addform.dias" type="number"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <el-button type="primary" icon="el-icon-success" @click="agregar()">Agregar</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-dialog>
  </el-container>
</template>

<script>
import BuscarColocacionPersonaComponent from '@/components/BuscarColocacionPersona'
import { buscarCredito } from '@/api/infocredito'
import { agregarPeriodoGracia } from '@/api/colperiodogracia'

export default {
  components: {
    'buscar-colocacion-persona': BuscarColocacionPersonaComponent
  },
  data() {
    return {
      id_identificacion: null,
      id_persona: null,
      id_colocacion: null,
      dataColocacion: [],
      addform: {
        id_colocacion: null,
        dias: null,
        fecha: new Date()
      },
      loading: false,
      showAddDlg: false
    }
  },
  methods: {
    tableRowClassName({ row, rowIndex }) {
      if (row.estado === 'JURIDICO') {
        return 'warning-row'
      } else if (row.estado === 'SALDADO') {
        return 'danger-row'
      }
      return ''
    },
    activarAgregar(id_colocacion) {
      this.addform.id_colocacion = id_colocacion
      this.showAddDlg = true
    },
    agregar() {
      this.showAddDlg = false
      agregarPeriodoGracia(this.addform.id_colocacion, this.addform.fecha.getTime(), this.addform.dias).then(response => {
        if (response.data === true) {
          this.$message({
            message: 'Periodo de Gracia Agregado con Exito.',
            type: 'success'
          })
        } else {
          this.$message({
            message: 'NO se pudo Agregar Periodo de Gracia',
            type: 'warning'
          })
        }
        this.addform = {
          dias: null,
          id_colocacion: null,
          fecha: new Date()
        }
        this.emitirEvento()
      }).catch(error => {
        this.$message({
          message: 'NO se pudo Agregar Periodo de Gracia - ' + error,
          type: 'warning'
        })
        this.addform = {
          dias: null,
          id_colocacion: null,
          fecha: new Date()
        }
      })
    },
    setDatosPersonaDesdeElEvento(data) {
      this.id_identificacion = data.id_identificacion
      this.id_persona = data.id_persona
      this.showBuscarPersonaDlg = false
      this.buscarColocaciones()
    },
    buscarColocaciones() {
      buscarCredito(this.id_identificacion, this.id_persona).then(response => {
        this.dataColocacion = response.data
      })
    },
    emitirEvento() {
      this.$emit('actualizarEvent')
    }
  }
}
</script>
<style>
  .el-table .warning-row {
    background: #E6A23C;
  }

  .el-table .success-row {
    background: #f0f9eb;
  }

  .el-table .danger-row {
    background: #F56C6C;
  }
</style>
