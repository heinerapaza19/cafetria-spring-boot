import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AdminSidebar } from '../../../shared/admin-sidebar/admin-sidebar';
import { CategoriaService } from '../../../core/services/categoria';
import { Categoria } from '../../../core/models';

@Component({
  selector: 'app-categorias',
  standalone: true,
  imports: [CommonModule, FormsModule, AdminSidebar],
  templateUrl: './categorias.html',
  styleUrl: './categorias.scss',
})
export class Categorias implements OnInit {
  private categoriaSvc = inject(CategoriaService);

  categorias = signal<Categoria[]>([]);
  loading = signal(false);
  showModal = signal(false);
  saving = signal(false);

  mensaje = signal('');
  mensajeTipo = signal<'success' | 'error'>('success');

  form = {
    id: 0,
    nombre: '',
    descripcion: '',
  };

  imagenFile: File | null = null;
  editMode = false;

  ngOnInit() {
    this.cargarCategorias();
  }

  cargarCategorias() {
    this.loading.set(true);

    this.categoriaSvc.getAll().subscribe({
      next: (data) => {
        this.categorias.set([...data]);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.mensaje.set('Error cargando categorías');
        this.mensajeTipo.set('error');
      },
    });
  }

  abrirModal(c?: Categoria) {
    this.editMode = !!c;

    this.form = c
      ? { id: c.id, nombre: c.nombre, descripcion: c.descripcion || '' }
      : { id: 0, nombre: '', descripcion: '' };

    this.imagenFile = null;
    this.showModal.set(true);
  }

  onFileChange(event: any) {
    this.imagenFile = event.target.files?.[0] || null;
  }

  guardar() {
    this.saving.set(true);

    const fd = new FormData();
    fd.append('nombre', this.form.nombre);
    fd.append('descripcion', this.form.descripcion);

    if (this.imagenFile) {
      fd.append('imagen', this.imagenFile);
    }

    const req = this.editMode
      ? this.categoriaSvc.update(this.form.id, fd)
      : this.categoriaSvc.create(fd);

    req.subscribe({
      next: () => {
        this.saving.set(false);
        this.showModal.set(false);
        this.cargarCategorias();

        this.mensaje.set(this.editMode ? 'Actualizado' : 'Creado');
        this.mensajeTipo.set('success');
      },
      error: () => {
        this.saving.set(false);
        this.mensaje.set('Error al guardar');
        this.mensajeTipo.set('error');
      },
    });
  }

  eliminar(id: number) {
    if (!confirm('¿Eliminar?')) return;

    this.categoriaSvc.delete(id).subscribe({
      next: () => {
        this.mensaje.set('Eliminado');
        this.mensajeTipo.set('success');
        this.cargarCategorias();
      },
      error: () => {
        this.mensaje.set('Error al eliminar');
        this.mensajeTipo.set('error');
      },
    });
  }

  // 🔥 ESTA ES LA CLAVE CORREGIDA
  getImgUrl(img?: string | null): string {
    if (!img) return '';

    return `http://localhost:7091/uploads/categorias/${img}`;
  }

  onImgError(event: Event) {
    const img = event.target as HTMLImageElement;
    img.src = 'assets/no-image.png'; // opcional fallback
  }
}
