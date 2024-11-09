require('dotenv').config();
var express = require('express');
var geohash = require('ngeohash');
var SpotifyWebApi = require('spotify-web-api-node');

var app = express();

app.use('/', express.static('client'))
app.use('/search', express.static('client'))

const cors = require('cors');
app.use(cors({
    origin: '*'
}));

var spotifyApi = new SpotifyWebApi({
  clientId: process.env.SPOTIFY_CLIENT_ID,
  clientSecret: process.env.SPOTIFY_CLIENT_SECRET
});

let API = process.env.TICKETMASTER_API_KEY;

async function getDataFromSpotify(artistTeam) {
   let artistobj = new Array;

   for(let artist of artistTeam) {
      
      await spotifyApi.searchArtists(artist)
      .then(data => {
         let idx = -1;
         if(data.body.hasOwnProperty('artists') && data.body.artists.hasOwnProperty('items')
         && data.body.artists.items.length != 0) {
            for(let i = 0; i<data.body.artists.items.length; i++) {
               if(artist.toLowerCase() ==  data.body.artists.items[i].name.toLowerCase()) {
                  idx = i;
                  break;
               }
            }
         }
         if(idx != -1) {
            let image_url = ''
            let total_followers = ''
            let total_popularity = ''
            let spotify_link = ''

            if(data.body.artists.items[idx].hasOwnProperty('images') 
            && data.body.artists.items[idx].images.length != 0 
            && data.body.artists.items[idx].images[0].hasOwnProperty('url')) {
               image_url = data.body.artists.items[idx].images[0].url
            }

            if(data.body.artists.items[idx].hasOwnProperty('followers') 
            && data.body.artists.items[idx].followers.hasOwnProperty('total')) {
               total_followers = data.body.artists.items[idx].followers.total
            }

            if(data.body.artists.items[idx].hasOwnProperty('popularity')) {
               total_popularity = data.body.artists.items[idx].popularity
            }

            if(data.body.artists.items[idx].hasOwnProperty('external_urls') 
            && data.body.artists.items[idx].external_urls.hasOwnProperty('spotify')) {
               spotify_link = data.body.artists.items[idx].external_urls.spotify
            }

            var obj = {
               'name': data.body.artists.items[idx].name,
               'followers':total_followers,
               'popularity': total_popularity,
               'spotify_link': spotify_link,
               'image': image_url,
               'id': data.body.artists.items[idx].id
            }
            return obj
         }
      })
      .then(async (obj) => {
         let images = []
            if(obj == undefined) return;
            await spotifyApi.getArtistAlbums(obj.id, {limit: 3}) 
            .then(data => {
               if(data.hasOwnProperty('body') && data['body'].hasOwnProperty('items')) {
                  for(let img of data.body.items) {
                     if(img.hasOwnProperty('images') && img['images'].length != 0)
                        images.push(img.images[0].url)
                  }
                  obj['albums'] = images
               }
            })
         artistobj.push(obj)   
      })
   }
   return artistobj
}

app.get('/search', (req, res) => {
   res.redirect('/')
})

app.get('/spotify', async (req, res) => {

   await spotifyApi.clientCredentialsGrant().then(data => {
      spotifyApi.setAccessToken(data.body['access_token']);
      spotifyApi.setRefreshToken(data.body['refresh_token']);
   }).catch(error => {
      console.log(error)
   });

   let artistTeam = JSON.parse(req.query.name)
   let artistobj = await getDataFromSpotify(artistTeam)
   artistobj = JSON.stringify(artistobj)
   res.send(artistobj)
})


app.get('/suggest', async function (req, res) {
   let keyword = req.query.keyword
   let url = `https://app.ticketmaster.com/discovery/v2/suggest?apikey=${API}&keyword=${keyword}` 
   let response = await fetch(url).then(res => res.json())
   res.send(response)
})

app.get('/search/events', async function (req, res) {
   let values = req.query
   
   let mappings = {'Music' : 'KZFzniwnSyZfZ7v7nJ', 'Sports': 'KZFzniwnSyZfZ7v7nE','Arts': 'KZFzniwnSyZfZ7v7na', 'Theatre': 'KZFzniwnSyZfZ7v7na', 'Film': 'KZFzniwnSyZfZ7v7nn', 'Miscellaneous':'KZFzniwnSyZfZ7v7n1', 'Default': ''}

    let keyword = values['keyword']
    let distance = values['distance']
    let segmentId = mappings[values['category']]
    let geo_hash = geohash.encode(values['latitude'], values['longitude'], 7)
     
    let url = `https://app.ticketmaster.com/discovery/v2/events.json?apikey=${API}&keyword=${keyword}&segmentId=${segmentId}&radius=${distance}&unit=miles&geoPoint=${geo_hash}`

   let response = await fetch(url).then(res => res.json())
   res.send(response)
})


app.get('/search/id', async function (req, res) {
   let values = req.query
   let eventid = values.eventid
     
   let url = `https://app.ticketmaster.com/discovery/v2/events/${eventid}?apikey=${API}&`

   let response = await fetch(url).then(res => res.json())
   res.send(response)
})

app.get('/search/venue', async function (req, res) {
   let values = req.query
   let venue = values.venue
     
   let url = `https://app.ticketmaster.com/discovery/v2/venues?apikey=${API}&keyword=${venue}`

   
   let response = await fetch(url).then(res => res.json())
   res.send(response)
})

const port = process.env.PORT || 8000;

var server = app.listen(port, function () {
   var host = server.address().address
   var port = server.address().port
   
   console.log(`Server listening at ${host}:${port}`)
})

