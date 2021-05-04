<template>
  <el-container>
    <el-header>
      <h2>Consulta de Auxiliar</h2>
    </el-header>
    <el-main>
      <el-form>  
        <el-row :gutter="6">
          <el-col :span="6">
            <el-form-item label="Codigo Inicial">
              <el-input v-model="ci" @blur="buscarCi()"/>
            </el-form-item>
          </el-col>
          <el-col :span="18">
            <el-form-item label="_">
              <el-input readonly v-model="cin" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="Codigo Final">
              <el-input v-model="cf" @blur="buscarCf()"/>
            </el-form-item>
          </el-col>
          <el-col :span="18">
            <el-form-item label="_">
              <el-input readonly v-model="cfn" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-form-item label="Fecha Inicial">
              <el-date-picker v-model="fi" />
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="Fecha Final">
              <el-date-picker v-model="ff" />
            </el-form-item>
          </el-col>
        </el-row>  
        <el-row :gutter="4">
          <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
            <el-form-item prop="id_identificacion" label="Tipo de Documento">
              <el-select v-model="id_identificacion" filterable>
                <el-option v-for="t in tipo_documento" :key="t.id_identificacion" :label="t.descripcion_identificacion" :value="t.id_identificacion" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
            <el-form-item prop="id_persona" label="Número de Documento">
              <el-input v-model="id_persona" @blur="buscarPersona"/>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
            <el-form-item label="Nombre">
              <el-input v-model="nombre" readonly />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
            <el-button type="primary" icon="el-icon-search" circle title="Buscar Por Apellidos y Nombre" @click="showBuscarPersonaDlg = true" />
          </el-col>
        </el-row>   
      </el-form>
    </el-main>
    <!-- Dialogo Buscar por Apellidos y Nombre -->
    <el-dialog :visible.sync="showBuscarPersonaDlg" title="Buscar Por Apellidos y Nombre" width="80%">
      <buscar-por-nombre @selectPersonEvent="setDatosPersonaDesdeElEvento" />
    </el-dialog>
  </el-container>
</template>
<script>
import BuscarPersonaComponent from '@/components/BuscarPersonaNombre'
import { obtenerListaTipoIdentificacion } from '@/api/tipos'
import { obtenerNombrePuc } from '@/api/puc'
import { obtenerPersona } from '@/api/persona'

export default {
  components: {
    'buscar-por-nombre': BuscarPersonaComponent
  },  
  data () {
    return {
      ci: '',
      cf: '',
      cin: null,
      cfn: null,
      fi: new Date(),
      ff: new Date(),
      id_identificacion: null,
      id_persona: null,
      showBuscarPersonaDlg: false,
      nombre: null,
      tipo_documento: null
    }
  },
  beforeMount() {
    obtenerListaTipoIdentificacion().then(response => {
      this.tipo_documento = response.data
    })
  },
  methods: {
    buscarCi () {
      while (this.ci.length < 18) this.ci = this.ci + "0"
      obtenerNombrePuc(this.ci).then(response => {
        this.cin = response.data
      })
    },
    buscarCf () {
      console.log("codigo ant:" + this.cf)
      while (this.cf.length < 18) this.cf = this.cf + "0"
      obtenerNombrePuc(this.cf).then(response => {
        this.cfn = response.data
      })
    },
    setDatosPersonaDesdeElEvento(data) {
      this.id_identificacion = data.id_identificacion
      this.id_persona = data.id_persona
      this.showBuscarPersonaDlg = false
      this.buscarPersona()
    },
    buscarPersona() {
      const loading = this.$loading({
        lock: true,
        text: 'Cargando...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
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
    }        
  }
}
</script>