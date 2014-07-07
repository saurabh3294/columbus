package resize;
use nginx;
use Image::Magick;
use LWP::Simple;
#use JSON::XS;
our $base_dir="/var/www";
our $image;


sub handler {
    my $r = shift;

    my $uri=$r->uri;
    my @tokens = split("/", $uri);
    my @idExtSplit = split(".", $tokens[12]);
    $srcPath = "/tmp/";
    $destPath = "/usr/share/nginx/html/tmp/";
    $randomValue = int(rand(10000));

    $width = $tokens[5];
    $height = $tokens[6];
    $quality = $tokens[7];
    $file_size = $tokens[8];
    $imageId = $idExtSplit[0];
    $extension = $idExtSplit[1];
    $srcFileName = $srcPath.$randomValue."_".$tokens[12];
    $destFileName = $destPath.$randomValue."_".$width."_".$height."_".$quality."_".$file_size."_".$tokens[12];    
 
    my $downloadImageUrl = $tokens[2]."//".$tokens[4]."/".$tokens[9]."/".$tokens[10]."/".$tokens[11]."/".$tokens[12];
    $downloadImageUrl =~ s/https:/http:/;

    open FILE, ">/tmp/file.txt" or die $!;
    print FILE $r->uri."\n";
    local $" = ', '; #"
    print FILE "@tokens"."\n";
    print FILE $downloadImageUrl."\n";
    close FILE;

    my $url = $downloadImageUrl;#"http://im.proptiger.com/1/1342/6/288083.jpeg?width=360&height=240";
    my $status = getstore($downloadImageUrl, $srcFileName);
    
    $image= new Image::Magick;
    $image->Read($srcFileName);
    $image->Scale(width=> $width, height=>$height);
    $image->Set(quality=> $quality, interlace=> "Plane", 'jpeg:extent' => $file_size);
    $image->Strip();
    $image->Write($destFileName);
    unlink($srcFileName);
    $r->sendfile($destFileName);
    $r->header_out("Content-Type", "image/".$extension);
    $r->header_out("Content-length", (stat($destFileName))[10]);
    $r->send_http_header("image/".$extension);
    return OK;

}

    #return DECLINED unless $r->uri =~ m/\.resize_to\.\d{1,}?x\d{1,}?\./;

    #$uri=~ s!^/resize!!;

    #my $dest_file="$base_dir/$uri";
    #my @path_tokens=split("/", $uri);
    #my $filename=pop @path_tokens;
    #my @filename_tokens=split('\.', $filename);

    # We know  the last part is the extension;
    # We know the one before that is the dimensions
    # We know that the one before that is the resize_to string

    #my $ext=pop @filename_tokens;
    #my $dimensions=pop @filename_tokens;
    #pop @filename_tokens;
    #$filename=join('.', @filename_tokens, $ext);

    #my $real_file_path=join("/",   $base_dir, @path_tokens, $filename);
    #return DECLINED unless -f $real_file_path;

    #my ($width,$height)=split("x", $dimensions);
    #if ($height<1) {
    #    $dimensions=$width;
    #}
    ## Download a File
1;
__END__
