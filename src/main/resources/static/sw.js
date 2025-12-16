const CACHE_NAME = 'grocer-cache-v1';
const ASSETS_TO_CACHE = [
    '/',
    '/index.html',
    '/css/styles.css',
    '/js/app.js',
    // other static files
];

// Install service worker and cache assets
self.addEventListener('install', event => {
    event.waitUntil(
        caches.open(CACHE_NAME).then(cache => cache.addAll(ASSETS_TO_CACHE))
    );
});

// Intercept fetch requests
self.addEventListener('fetch', event => {
    if (event.request.url.startsWith('http://localhost') || event.request.url.startsWith('https://localhost')) {
        // Optionally: cache API responses for offline use
        event.respondWith(
            fetch(event.request)
                .then(response => {
                    // Clone and cache response
                    const resClone = response.clone();
                    caches.open(CACHE_NAME).then(cache => cache.put(event.request, resClone));
                    return response;
                })
                .catch(() => caches.match(event.request))
        );
    } else {
        // Serve cached static assets
        event.respondWith(
            caches.match(event.request).then(response => response || fetch(event.request))
        );
    }
});
