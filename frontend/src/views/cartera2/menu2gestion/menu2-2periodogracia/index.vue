<template>
  <el-container>
    <el-header>
      <el-row>
        <el-col :span="24">
          <h1>Control Periodo de Gracia</h1>
        </el-col>
      </el-row>
    </el-header>
    <el-main>
      <el-row>
        <el-col :span="24">
          <div style="text-align: center;"><h3>Colocaciones</h3></div>
        </el-col>
      </el-row>
      <el-row :gutter="4">
        <el-col :span="4">
          <el-button type="success" circle icon="el-icon-refresh" title="Recargar Datos" @click="refrescar"/>
        </el-col>
        <el-col :span="4">
          <span>Buscar Colocación</span>
        </el-col>
        <el-col :span="4">
          <el-input v-model="filtro" type="number"/>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-table v-loading="loading" :data="dataColocacion.filter(data => !filtro || data.id_colocacion.includes(filtro.toLowerCase()))" :row-class-name="tableRowClassName" style="width: 100%; font-size: 12px;" max-height="450" @current-change="handleCurrentChange" >
            <el-table-column type="selection" width="55" />
            <el-table-column sortable label="Colocación" prop="id_colocacion" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.id_colocacion }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Nombre" prop="nombre" width="350">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.nombre }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Saldo" prop="saldo" align="right" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.saldo | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Cuota" prop="cuota" align="right" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.cuota | currency }}</span>
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
            <el-table-column label="Registro" prop="fecha_registro" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_registro | moment("YYYY-MM-DD") }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="D.Gracia" prop="dias" width="100" align="right">
              <template slot-scope="scope">
                <template v-if="scope.row.edit">
                  <el-input v-model="scope.row.dias" class="edit-input" size="small" />
                  <el-button
                    class="cancel-btn"
                    size="mini"
                    icon="el-icon-close"
                    type="warning"
                    circle
                    @click="cancelEdit(scope.row)"
                  />
                </template>
                <span v-else>{{ scope.row.dias }}</span>
                <el-button
                  v-if="scope.row.edit"
                  circle
                  size="mini"
                  icon="el-icon-check"
                  @click="confirmEdit(scope.row)"
                />
                <el-button
                  v-else-if="scope.row.estado === 0"
                  circle
                  size="mini"
                  icon="el-icon-edit"
                  style="border-style: hidden;"
                  @click="scope.row.edit=!scope.row.edit"
                />
              </template>
            </el-table-column>
            <el-table-column sortable label="Causa" prop="se_causa" width="80">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.se_causa }}</span>
              </template>
            </el-table-column>
            <el-table-column label="F.Estado" prop="fecha_cancelado" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_cancelado | moment("YYYY-MM-DD") }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Estado" prop="estado" width="120">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ estado(scope.row.estado) }}</span>
              </template>
            </el-table-column>
            <el-table-column
              fixed="right"
              label="Ver"
              width="140">
              <template slot-scope="scope">
                <el-dropdown :split-button="true" size="mini" type="primary" trigger="click">
                  Acciones
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item v-if="scope.row.estado === 0"><div @click="confirmarNormalizar(scope.row.id_colocacion)"><i class="el-icon-success" title="Normalizar Colocación" />Normalizar</div></el-dropdown-item>
                    <el-dropdown-item v-if="scope.row.estado === 2"><div @click="confirmarNormalizarReverso(scope.row.id_colocacion)"><i class="el-icon-refresh" title="Finalizar Periodo" />Reversar</div></el-dropdown-item>
                    <el-dropdown-item v-if="scope.row.estado === 0"><div @click="confirmarEliminar(scope.row.id_colocacion)"><i class="el-icon-delete" title="Eliminar Registro" />Eliminar</div></el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-button type="primary" @click="showAddDialog=true">Agregar</el-button>
        </el-col>
      </el-row>
    </el-main>
    <!-- Dialogo Buscar por Apellidos y Nombre -->
    <el-dialog :visible.sync="showBuscarPersonaDlg" title="Buscar Por Apellidos y Nombre" width="80%">
      <buscar-por-nombre @selectPersonEvent="setDatosPersonaDesdeElEvento" />
    </el-dialog>
    <!-- Dialogo confirmar normalizar -->
    <el-dialog :visible.sync="confirmacionNormalizar" title="Confirmación" >
      <span style="font-size:20px;">Seguro de Normalizar la Colocación {{ id_colocacion }}?</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="confirmacionNormalizar = false">No</el-button>
        <el-button type="primary" @click="normalizar">Sí</el-button>
      </span>
    </el-dialog>
    <!-- Dialogo confirmar normalizar -->
    <el-dialog :visible.sync="confirmacionNormalizarReverso" title="Confirmación" width="70%">
      <span style="font-size:20px;">Seguro de Reversar Normalización de la Colocación {{ id_colocacion }}?</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="confirmacionNormalizarReverso = false">No</el-button>
        <el-button type="primary" @click="reverso">Sí</el-button>
      </span>
    </el-dialog>
    <!-- Dialogo confirmar eliminar -->
    <el-dialog :visible.sync="confirmacionEliminar" title="Confirmación" >
      <span style="font-size:20px;">Seguro de Eliminar el Periodo de Gracia  {{ id_colocacion }}?</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="confirmacionEliminar = false">No</el-button>
        <el-button type="primary" @click="eliminar">Sí</el-button>
      </span>
    </el-dialog>
    <!-- Agregar periodo de gracia -->
    <el-dialog :visible.sync="showAddDialog" :destroy-on-close="true" title="Agregar Periódo de Gracia" width="80%" style="padding: 0px 0px !important">
      <agregar-periodo-gracia @actualizarEvent="actualizarDespuesDeAgregar()" />
    </el-dialog>
  </el-container>

</template>
<script>
import BuscarPersonaComponent from '@/components/BuscarPersonaNombre'
import AgregarPeriodoGraciaComponent from '@/components/AgregarPeriodoGracia'
import { obtenerListaAsesores } from '@/api/tipos'
import { obtenerTodos, buscarPeriodoGraciaPorDocumento, normalizarColocacionPeriodoGracia,
  normalizarreversoColocacionPeriodoGracia, eliminarPeriodoGracia,
  actualizarDiasPeriodoGracia } from '@/api/colperiodogracia'
import { obtenerPersona } from '@/api/persona'

export default {
  components: {
    'buscar-por-nombre': BuscarPersonaComponent,
    'agregar-periodo-gracia': AgregarPeriodoGraciaComponent
  },
  data() {
    return {
      id_identificacion: null,
      id_persona: null,
      id_colocacion: null,
      dataColocacion: [],
      filtro: null,
      nombre: null,
      nombrecompleto: '',
      ases_id: -1,
      tipo_documento: null,
      loading: false,
      asesores: [],
      showBuscarPersonaDlg: false,
      showAddDialog: false,
      confirmacionNormalizar: false,
      confirmacionEliminar: false,
      confirmacionNormalizarReverso: false,
      loader: null,
      estadoPeriodoGracia: [
        { value: 0, label: 'ACTIVO' },
        { value: 2, label: 'NORMALIZADO' },
        { value: 8, label: 'FINALIZADO' },
        { value: 9, label: 'ELIMINADO' }
      ]
    }
  },
  beforeMount() {
    obtenerListaAsesores().then(response => {
      this.asesores = response.data
      this.asesores.unshift({ id: -1, descripcion: 'TODOS' })
      this.refrescar()
    }).catch(error => {
      this.$alert(error, 'Error Consultando Lista Asesores')
    })
  },
  methods: {
    handleCurrentChange(colocacion) {
      this.colocacion = colocacion
    },
    tableRowClassName({ row, rowIndex }) {
      if (row.estado === 8) {
        return 'warning-row'
      } else if (row.estado === 9) {
        return 'danger-row'
      } else if (row.estado === 2) {
        return 'yellow-row'
      }
      return ''
    },
    confirmarNormalizar(id_colocacion) {
      this.id_colocacion = id_colocacion
      this.confirmacionNormalizar = true
    },
    confirmarNormalizarReverso(id_colocacion) {
      this.id_colocacion = id_colocacion
      this.confirmacionNormalizarReverso = true
    },
    confirmarEliminar(id_colocacion) {
      this.id_colocacion = id_colocacion
      this.confirmacionEliminar = true
    },
    normalizar() {
      this.confirmacionNormalizar = false
      normalizarColocacionPeriodoGracia(this.id_colocacion).then(response => {
        if (response.data === true) {
          this.$alert('Periodo de Gracia Normalizado', 'Normalizar Periodo Gracia')
          this.refrescar()
        } else {
          this.$alert('NO se puedo normalizar el periodo de gracia', 'Normalizar Periodo Gracia')
        }
        this.id_colocacion = null
      }).catch(() => {
        this.id_colocacion = null
      })
    },
    reverso() {
      this.confirmacionNormalizarReverso = false
      normalizarreversoColocacionPeriodoGracia(this.id_colocacion).then(response => {
        if (response.data === true) {
          this.$alert('Periodo de Gracia Reactivado', 'Reverso Normalizar Periodo Gracia')
          this.refrescar()
        } else {
          this.$alert('NO se puedo reactivar el periodo de gracia', 'Reverso Normalizar Periodo Gracia')
        }
        this.id_colocacion = null
      }).catch(() => {
        this.id_colocacion = null
      })
    },
    eliminar() {
      this.confirmacionEliminar = false
      eliminarPeriodoGracia(this.id_colocacion).then(response => {
        if (response.data === true) {
          this.$alert('Periodo de Gracia Eliminado', 'Eliminar Periodo Gracia')
        } else {
          this.$alert('NO se puedo eliminar el periodo de gracia', 'Eliminar Periodo Gracia')
        }
        this.id_colocacion = null
      }).catch(() => {
        this.id_colocacion = null
      })
    },
    estado(id) {
      if (id !== undefined && id !== null) {
        return this.estadoPeriodoGracia.find(e => e.value === id, { label: 'NO DEFINIDO' }).label
      } else {
        return 'NO DEFINIDO'
      }
    },
    setDatosPersonaDesdeElEvento(data) {
      this.id_identificacion = data.id_identificacion
      this.id_persona = data.id_persona
      this.showBuscarPersonaDlg = false
    },
    refrescar() {
      this.mostrarLoader()
      obtenerTodos().then(response => {
        this.dataColocacion = response.data.map(v => {
          this.$set(v, 'edit', false) // https://vuejs.org/v2/guide/reactivity.html
          this.$set(v, 'diasOriginal', v.dias)
          return v
        })
        this.ocultarLoader()
      }).catch(error => {
        this.ocultarLoader()
        this.$alert(error, 'Error Buscando Colocaciones con Periodo de Gracia')
      })
    },
    cancelEdit(row) {
      row.dias = row.diasOriginal
      row.edit = false
      this.$message({
        message: 'Los días se restauraron a su valor original',
        type: 'warning'
      })
    },
    confirmEdit(row) {
      row.edit = false
      actualizarDiasPeriodoGracia(row.id_colocacion, row.dias).then(response => {
        if (response.data === true) {
          row.precioOriginal = row.dias
          this.$message({
            message: 'Los días fueron actualizados',
            type: 'success'
          })
        } else {
          row.dias = row.diasOriginal
          this.$message({
            message: 'Los días se restauraron a su valor original',
            type: 'warning'
          })
        }
      }).catch(() => {
        row.dias = row.diasOriginal
        this.$message({
          message: 'Los días se restauraron a su valor original',
          type: 'warning'
        })
      })
    },
    buscarPorDocumento() {
      this.loading = true
      this.limpiarTablas()
      buscarPeriodoGraciaPorDocumento(this.id_identificacion, this.id_persona).then(response => {
        this.dataColocacion = response.data.map(v => {
          this.$set(v, 'edit', false) // https://vuejs.org/v2/guide/reactivity.html
          this.$set(v, 'diasOriginal', v.dias)
          return v
        })
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    buscarPersona() {
      const loading = this.$loading({
        lock: true,
        text: 'Cargando...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      this.limpiarTablas()
      obtenerPersona(this.id_identificacion, this.id_persona).then(response => {
        loading.close()
        if (response.status === 200) {
          this.nombre = response.data.a.nombre + ' ' + response.data.a.primer_apellido + ' ' + response.data.a.segundo_apellido
        }
      }).catch(() => {
        this.nombre = null
        loading.close()
        this.$alert('Persona No Existe', 'Buscando Persona')
      })
    },
    limpiarTablas() {
      this.dataColocacion = []
      this.id_colocacion = null
    },
    mostrarLoader() {
      this.loader = this.$loading({
        lock: true,
        text: 'Cargando',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    ocultarLoader() {
      if (this.loader) {
        this.loader.close()
      }
    },
    actualizarDespuesDeAgregar() {
      console.log('cerrando agregar')
      this.showAddDialog = false
      this.refrescar()
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

  .el-table .yellow-row {
    background: #f0f9eb;
  }
</style>
