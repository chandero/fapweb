<template>
  <el-container>
    <el-main>
      <el-row :gutter="4">
        <el-col>
          <el-tabs type="border-card">
            <el-tab-pane>
              <span slot="label"><i class="el-icon-search" />Colocación</span>
              <el-form label-position="left">
                <el-row>
                  <el-col :xs="24" :sm="20" :md="14" :lg="14" :xl="14">
                    <el-form-item label="Colocacion">
                      <el-input v-model="id_colocacion" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="20" :md="10" :lg="10" :xl="10">
                    <el-form-item>
                      <el-button :disabled="id_colocacion === null || id_colocacion === ''" type="success" icon="el-icon-search" title="Buscar Datos" @click="buscarPorColocacion">Buscar</el-button>
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </el-tab-pane>
            <el-tab-pane>
              <span slot="label"><i class="el-icon-user-solid" />Buscar Por Documento</span>
              <el-form label-position="left">
                <el-row :gutter="4">
                  <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
                    <el-form-item prop="id_identificacion" label="Tipo de Documento">
                      <el-select v-model="data.id_identificacion" filterable>
                        <el-option v-for="t in tipo_documento" :key="t.id_identificacion" :label="t.descripcion_identificacion" :value="t.id_identificacion" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                    <el-form-item prop="id_persona" label="Número de Documento">
                      <el-input v-model="data.id_persona" @blur="buscarPersona"/>
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                    <el-button type="primary" icon="el-icon-search" circle title="Buscar Por Apellidos y Nombre" @click="showBuscarPersonaDlg = true" />
                  </el-col>
                </el-row>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
      <el-row :gutter="4">
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
          <h5>Nombre: {{ nombre }}</h5>
        </el-col>
      </el-row>
    </el-main>
    <!-- Dialogo Buscar por Apellidos y Nombre -->
    <el-dialog :visible.sync="showBuscarPersonaDlg" :destroy-on-close="true" append-to-body title="Buscar Por Apellidos y Nombre" width="80%">
      <buscar-por-nombre @selectPersonEvent="setDatosPersonaDesdeElEvento" />
    </el-dialog>
  </el-container>
</template>
<script>
import BuscarPersonaComponent from '@/components/BuscarPersonaNombre'
import { obtenerListaTipoIdentificacion } from '@/api/tipos'
import { obtenerPersona, obtenerPersonaPorColocacion } from '@/api/persona'

export default {
  name: 'BuscarColocacionPersonaComponent',
  components: {
    'buscar-por-nombre': BuscarPersonaComponent
  },
  props: {
    tipo: {
      type: Number,
      required: false,
      default: 0
    },
    documento: {
      type: String,
      required: false,
      default: ''
    },
    colocacion: {
      type: String,
      required: false,
      default: ''
    }
  },
  data() {
    return {
      tipo_documento: [],
      nombre: null,
      showBuscarPersonaDlg: false,
      persona: [],
      data: {
        id_identificacion: null,
        id_persona: null
      },
      id_colocacion: null,
      loading: false
    }
  },
  beforeMount() {
    this.data.id_identificacion = this.tipo
    this.data.id_persona = this.documento
    this.id_colocacion = this.colocacion
    this.nombre = null
    obtenerListaTipoIdentificacion().then(response => {
      this.tipo_documento = response.data
    })
  },
  methods: {
    setDatosPersonaDesdeElEvento(data) {
      this.data.id_identificacion = data.id_identificacion
      this.data.id_persona = data.id_persona
      this.showBuscarPersonaDlg = false
      this.buscarPersona()
    },
    limpiarTablas() {
      this.persona = null
      this.nombre = null
    },
    buscarPorColocacion() {
      const loading = this.$loading({
        lock: true,
        text: 'Cargando...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      obtenerPersonaPorColocacion(this.id_colocacion).then(response => {
        this.persona = response.data
        if (this.persona) {
          this.nombre = this.persona.a.nombre + ' ' + this.persona.a.primer_apellido + ' ' + this.persona.a.segundo_apellido
          this.data.id_identificacion = this.persona.a.id_identificacion
          this.data.id_persona = this.persona.a.id_persona
          this.emitirEvento()
        } else {
          this.nombre = null
          this.$alert('No se encontró registro con esa Colocación', 'Buscando Persona')
        }
        loading.close()
      }).catch(() => {
        this.nombre = null
        loading.close()
      })
    },
    buscarPersona() {
      const loading = this.$loading({
        lock: true,
        text: 'Cargando...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      this.limpiarTablas()
      obtenerPersona(this.data.id_identificacion, this.data.id_persona).then(response => {
        loading.close()
        if (response.status === 200) {
          this.nombre = response.data.a.nombre + ' ' + response.data.a.primer_apellido + ' ' + response.data.a.segundo_apellido
          this.emitirEvento()
        }
      }).catch(() => {
        this.nombre = null
        loading.close()
        this.$alert('Persona No Existe', 'Buscando Persona')
      })
    },
    emitirEvento() {
      this.$emit('setPersonEvent', this.data)
    }
  }
}
</script>
