import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Header } from '../../shared/header/header';
import { Footer } from '../../shared/footer/footer';

@Component({
  selector: 'app-gallery',
  standalone: true,
  imports: [CommonModule, Header, Footer],
  templateUrl: './gallery.html',
  styleUrl: './gallery.scss',
})
export class Gallery {
  // 🔥 imágenes reales (puedes cambiar luego por backend)
  images = [
    'https://images.unsplash.com/photo-1509042239860-f550ce710b93',
    'https://images.unsplash.com/photo-1507133750040-4a8f57021571',
    'https://images.unsplash.com/photo-1442512595331-e89e73853f31',
    'https://images.unsplash.com/photo-1511920170033-f8396924c348',
    'https://images.unsplash.com/photo-1521017432531-fbd92d768814',
    'https://images.unsplash.com/photo-1504754524776-8f4f37790ca0',
  ];
}
